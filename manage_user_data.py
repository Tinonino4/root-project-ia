#!/usr/bin/env python3
import argparse
import json
import os
import subprocess
import sys

VPS_HOST = "root@37.27.197.244"
DB_CONTAINER = "root-project-ia-db"
DB_USER = "postgres"
DB_NAME = "root_ia_db"

def run_remote_query(query):
    """Executes a SQL query on the remote VPS database container and returns the output."""
    ssh_cmd = [
        "ssh", VPS_HOST,
        f"docker exec -i {DB_CONTAINER} psql -U {DB_USER} -d {DB_NAME} -t -A -c \"{query}\""
    ]
    result = subprocess.run(ssh_cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    if result.returncode != 0:
        print(f"Error executing remote query: {result.stderr}", file=sys.stderr)
        sys.exit(1)
    return result.stdout.strip()

def run_remote_command(command_str):
    """Executes a SQL command (like DELETE) on the remote VPS database container."""
    ssh_cmd = [
        "ssh", VPS_HOST,
        f"docker exec -i {DB_CONTAINER} psql -U {DB_USER} -d {DB_NAME} -c \"{command_str}\""
    ]
    result = subprocess.run(ssh_cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    if result.returncode != 0:
        print(f"Error executing remote command: {result.stderr}", file=sys.stderr)
        sys.exit(1)
    return result.stdout.strip()

def format_sql_value(val):
    """Formats Python values to match PostgreSQL SQL literals."""
    if val is None:
        return "NULL"
    elif isinstance(val, bool):
        return "TRUE" if val else "FALSE"
    elif isinstance(val, (int, float)):
        return str(val)
    elif isinstance(val, (dict, list)):
        # For JSON / JSONB columns
        escaped = json.dumps(val).replace("'", "''")
        return f"'{escaped}'::jsonb"
    else:
        # For Strings, UUIDs, Dates, Timestamps, etc.
        escaped = str(val).replace("'", "''")
        return f"'{escaped}'"

def generate_inserts(table, rows):
    """Generates a block of INSERT statements for a given list of rows (dicts) in a table."""
    if not rows:
        return f"-- No records found for table {table}\n"
    
    statements = [f"-- Data for table {table}"]
    cols = list(rows[0].keys())
    col_str = ", ".join(cols)
    
    for row in rows:
        val_str = ", ".join(format_sql_value(row[col]) for col in cols)
        statements.append(f"INSERT INTO {table} ({col_str}) VALUES ({val_str});")
        
    return "\n".join(statements) + "\n\n"

def backup_user(email, output_path):
    print(f"Fetching user details for {email} from VPS...")
    
    # 1. Fetch user by email
    user_query = f"SELECT COALESCE(json_agg(t)::text, '[]') FROM (SELECT * FROM users WHERE email = '{email}') t;"
    user_json = run_remote_query(user_query)
    users = json.loads(user_json)
    
    if not users:
        print(f"Error: User with email '{email}' not found on VPS database.", file=sys.stderr)
        sys.exit(1)
        
    user = users[0]
    user_id = user["id"]
    print(f"Found user. ID: {user_id}, Name: {user.get('name')}, Provider: {user.get('provider')}")
    
    sql_content = []
    sql_content.append(f"-- Backup for user: {email}")
    sql_content.append(f"-- Generated on: {subprocess.check_output(['date']).decode('utf-8').strip()}")
    sql_content.append(f"-- User ID: {user_id}\n")
    
    # Add deletion header so restoring automatically purges conflicting users
    sql_content.append(f"-- Clean up existing record before restoring")
    sql_content.append(f"DELETE FROM users WHERE email = '{email}';\n")
    
    # 2. Backup users
    sql_content.append(generate_inserts("users", users))
    
    # 3. Backup user_profiles
    profile_query = f"SELECT COALESCE(json_agg(t)::text, '[]') FROM (SELECT * FROM user_profiles WHERE user_id = '{user_id}') t;"
    profiles = json.loads(run_remote_query(profile_query))
    sql_content.append(generate_inserts("user_profiles", profiles))
    
    # 4. Backup experiences
    experience_query = f"SELECT COALESCE(json_agg(t)::text, '[]') FROM (SELECT * FROM experiences WHERE user_id = '{user_id}') t;"
    experiences = json.loads(run_remote_query(experience_query))
    sql_content.append(generate_inserts("experiences", experiences))
    
    # 5. Backup cache_requests
    requests_query = f"SELECT COALESCE(json_agg(t)::text, '[]') FROM (SELECT * FROM cache_requests WHERE user_id = '{user_id}') t;"
    requests = json.loads(run_remote_query(requests_query))
    sql_content.append(generate_inserts("cache_requests", requests))
    
    # 6. Backup feedback_responses
    responses_query = f"SELECT COALESCE(json_agg(t)::text, '[]') FROM (SELECT * FROM feedback_responses WHERE cache_request_id IN (SELECT id FROM cache_requests WHERE user_id = '{user_id}')) t;"
    responses = json.loads(run_remote_query(responses_query))
    sql_content.append(generate_inserts("feedback_responses", responses))
    
    # 7. Backup user_skills_metrics
    metrics_query = f"SELECT COALESCE(json_agg(t)::text, '[]') FROM (SELECT * FROM user_skills_metrics WHERE user_id = '{user_id}') t;"
    metrics = json.loads(run_remote_query(metrics_query))
    sql_content.append(generate_inserts("user_skills_metrics", metrics))
    
    # 8. Backup user_otps
    otps_query = f"SELECT COALESCE(json_agg(t)::text, '[]') FROM (SELECT * FROM user_otps WHERE user_id = '{user_id}') t;"
    otps = json.loads(run_remote_query(otps_query))
    sql_content.append(generate_inserts("user_otps", otps))
    
    # 9. Backup refresh_tokens
    tokens_query = f"SELECT COALESCE(json_agg(t)::text, '[]') FROM (SELECT * FROM refresh_tokens WHERE user_id = '{user_id}') t;"
    tokens = json.loads(run_remote_query(tokens_query))
    sql_content.append(generate_inserts("refresh_tokens", tokens))
    
    with open(output_path, "w") as f:
        f.write("\n".join(sql_content))
        
    print(f"Backup successfully saved to '{output_path}'")

def delete_user(email, force=False):
    # Check if user exists
    user_query = f"SELECT COALESCE(json_agg(t)::text, '[]') FROM (SELECT id FROM users WHERE email = '{email}') t;"
    users = json.loads(run_remote_query(user_query))
    
    if not users:
        print(f"Warning: User with email '{email}' does not exist in the database.", file=sys.stderr)
        return
        
    if not force:
        confirm = input(f"Are you sure you want to delete user '{email}' and all associated records? (y/N): ")
        if confirm.lower() != 'y':
            print("Deletion cancelled.")
            return
            
    print(f"Deleting user '{email}' and all related records from VPS database...")
    delete_query = f"DELETE FROM users WHERE email = '{email}';"
    result = run_remote_command(delete_query)
    print(f"Deletion result: {result}")
    print("User and all cascade-deleted records have been removed successfully.")

def restore_user(sql_file_path):
    if not os.path.exists(sql_file_path):
        print(f"Error: Backup SQL file '{sql_file_path}' does not exist.", file=sys.stderr)
        sys.exit(1)
        
    print(f"Restoring user records from '{sql_file_path}' to remote VPS database...")
    
    ssh_cmd = [
        "ssh", VPS_HOST,
        f"docker exec -i {DB_CONTAINER} psql -U {DB_USER} -d {DB_NAME}"
    ]
    
    with open(sql_file_path, "r") as f:
        result = subprocess.run(ssh_cmd, stdin=f, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
        
    if result.returncode != 0:
        print(f"Error during restore: {result.stderr}", file=sys.stderr)
        sys.exit(1)
        
    print("Restore completed successfully!")
    print(result.stdout)

def main():
    parser = argparse.ArgumentParser(description="Manage user data on VPS: backup, delete, and restore.")
    subparsers = parser.add_subparsers(dest="command", required=True, help="Subcommand to run")
    
    # Backup parser
    parser_backup = subparsers.add_parser("backup", help="Back up user records to a local SQL file")
    parser_backup.add_argument("email", help="Email of the user to back up")
    parser_backup.add_argument("-o", "--output", help="Output file path (default: backup_<email>.sql)")
    
    # Delete parser
    parser_delete = subparsers.add_parser("delete", help="Delete user and all cascade data from VPS database")
    parser_delete.add_argument("email", help="Email of the user to delete")
    parser_delete.add_argument("-y", "--yes", action="store_true", help="Skip confirmation prompt")
    
    # Restore parser
    parser_restore = subparsers.add_parser("restore", help="Restore user records from a local SQL file")
    parser_restore.add_argument("file", help="Path to the local SQL backup file")
    
    args = parser.parse_args()
    
    if args.command == "backup":
        output_file = args.output or f"backup_{args.email}.sql"
        backup_user(args.email, output_file)
    elif args.command == "delete":
        delete_user(args.email, args.yes)
    elif args.command == "restore":
        restore_user(args.file)

if __name__ == "__main__":
    main()
