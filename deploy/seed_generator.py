#!/usr/bin/env python3
import random
import uuid
from datetime import datetime, timedelta

def generate_seed():
    # Pre-calculated BCrypt hash for the password "password123"
    bcrypt_password_hash = "$2a$10$mw.3LsS7MqvrS2tY2.V2TeyXsOpEegKdMMuYm7cv7EN47gQXTenxC"

    # Lists for generating realistic Spanish/European profiles
    first_names = [
        "Carlos", "Marta", "Javier", "Lucía", "Alejandro", "Elena", "Santiago", "Sofia",
        "Manuel", "Laura", "David", "Carmen", "Adrian", "Ana", "Pablo", "Isabel",
        "Alvaro", "Maria", "Jorge", "Cristina", "Ruben", "Sara", "Diego", "Paula",
        "Raul", "Irene", "Fernando", "Beatriz", "Ivan", "Raquel", "Hugo", "Natalia",
        "Marcos", "Clara", "Mario", "Alba", "Gabriel", "Julia", "Hector", "Marina",
        "Jose", "Silvia", "Daniel", "Patricia", "Angel", "Alicia", "Victor", "Andrea",
        "Miguel", "Celia"
    ]
    
    surnames = [
        "García", "Rodríguez", "González", "Fernández", "López", "Martínez", "Sánchez",
        "Pérez", "Gómez", "Martín", "Jiménez", "Ruiz", "Hernández", "Diaz", "Moreno",
        "Muñoz", "Alvarez", "Romero", "Alonso", "Gutiérrez", "Navarro", "Torres",
        "Domínguez", "Vázquez", "Ramos", "Gil", "Ramírez", "Serrano", "Blanco", "Molina",
        "Morales", "Suarez", "Ortega", "Delgado", "Castro", "Ortiz", "Rubio", "Marin",
        "Sanz", "Núñez", "Iglesias", "Medina", "Garrido", "Cortes", "Castillo", "Santos"
    ]

    cities = ["Madrid", "Barcelona", "Valencia", "Sevilla", "Zaragoza", "Málaga", "Bilbao", "Alicante"]

    companies = [
        "Google", "Meta", "Amazon", "Inditex", "Telefónica", "Santander", "BBVA",
        "Glovo", "Cabify", "Factorial", "Singular", "Idealista", "Mercadona", "Iberdrola"
    ]

    # Professional sectors definition
    sectors = {
        "IT_DEV": [
            ("Frontend Developer", "Ingeniería de Software", "Desarrollo de interfaces de usuario modernas con React y Vue. Colaboración con diseñadores UX/UI."),
            ("Backend Developer", "Ingeniería de Software", "Construcción de APIs robustas en Java y Spring Boot. Optimización de consultas PostgreSQL."),
            ("Fullstack Engineer", "Ingeniería de Software", "Desarrollo end-to-end de aplicaciones web. Despliegues en AWS y Docker."),
            ("DevOps Engineer", "Sistemas e Infraestructura", "Mantenimiento de pipelines CI/CD. Monitoreo en Kubernetes y aprovisionamiento con Terraform."),
            ("QA Automation Engineer", "Control de Calidad", "Creación de frameworks de pruebas automáticas con Selenium y Cypress."),
            ("Data Scientist", "Inteligencia Artificial", "Modelado de datos para predicción de comportamiento de usuarios. Python y TensorFlow."),
            ("Mobile Developer", "Mobile", "Desarrollo nativo de aplicaciones iOS (Swift) y Android (Kotlin)."),
            ("Cloud Architect", "Sistemas e Infraestructura", "Diseño de arquitecturas tolerantes a fallos en la nube. AWS y GCP.")
        ],
        "IT_MGMT": [
            ("Product Manager", "Producto", "Definición del roadmap del producto. Análisis de métricas clave e investigación de mercado."),
            ("Project Manager", "Gestión de Proyectos", "Coordinación de tiempos, recursos y entregas para múltiples equipos multidisciplinares."),
            ("Scrum Master", "Agile", "Facilitador de ceremonias Scrum. Eliminación de bloqueos y fomento de prácticas ágiles."),
            ("UX/UI Designer", "Diseño y Experiencia", "Creación de wireframes, flujos de usuario y prototipos interactivos de alta fidelidad en Figma."),
            ("Engineering Manager", "Dirección Técnica", "Liderazgo técnico y mentoría de desarrolladores. Alineamiento de objetivos de negocio."),
            ("Product Designer", "Diseño y Experiencia", "Concepto de producto centrado en el usuario, investigación de mercado y diseño visual.")
        ],
        "NON_IT": [
            ("HR Talent Acquisition Specialist", "Recursos Humanos", "Búsqueda y contratación de talento tecnológico. onboarding de candidatos."),
            ("Sales Representative", "Ventas y Negocio", "Prospección de clientes B2B. Negociación y cierre de acuerdos comerciales."),
            ("Marketing Manager", "Marketing y Comunicación", "Gestión de campañas publicitarias digitales (SEO/SEM) y estrategias de marca."),
            ("Account Executive", "Ventas y Negocio", "Relación directa con grandes cuentas de la compañía, retención y up-selling."),
            ("Operations Coordinator", "Operaciones", "Optimización de procesos internos logísticos e internos de la organización."),
            ("Recruiter", "Recursos Humanos", "Gestión de procesos de selección de principio a fin, cribado curricular y entrevistas."),
            ("Customer Success Specialist", "Atención al Cliente", "Acompañamiento a clientes post-venta para garantizar el máximo retorno del producto."),
            ("Office Manager", "Administración", "Coordinación del bienestar de la oficina física, compras y soporte administrativo general.")
        ]
    }

    # Universities for realistic education strings
    universities = [
        "Universidad Politécnica de Madrid", "Universidad de Barcelona", "Universitat Politècnica de Catalunya",
        "Universidad Complutense de Madrid", "Universidad de Valencia", "Universidad de Sevilla",
        "IE Business School", "ESADE", "Universidad de Deusto"
    ]

    about_me_templates = [
        "Profesional apasionado con más de {years} años de experiencia en el sector. Enfocado en la mejora continua y el trabajo en equipo.",
        "Orientado a resultados con trayectoria demostrada en {sector_desc}. Me encanta afrontar nuevos retos y resolver problemas complejos de forma creativa.",
        "Especialista en mi área, enfocado en aportar valor y eficiencia. Disfruto de la colaboración interdisciplinar y el liderazgo de iniciativas de impacto.",
        "Profesional proactivo en constante aprendizaje de nuevas metodologías. Comprometido con la excelencia y la integridad en las relaciones de trabajo."
    ]

    out_sql = []
    
    # 1. Clean previous seed data cleanly
    out_sql.append("/* ========================================================= */")
    out_sql.append("/* CLEAN PREVIOUS SEED DATA (CASCADE DELETES ALL)            */")
    out_sql.append("/* ========================================================= */")
    out_sql.append("DELETE FROM users WHERE email LIKE 'seed.%';\n")

    out_sql.append("/* ========================================================= */")
    out_sql.append("/* INSERTING 50 REALISTIC USERS, EXPERIENCES & FEEDBACK      */")
    out_sql.append("/* ========================================================= */")

    # Keep track of generated IDs for generating feedback links
    all_users = []

    # Map categories to their IDs from migration V3
    categories = {
        "TEAMWORK": "a1000000-0000-0000-0000-000000000001",
        "PROACTIVITY": "a1000000-0000-0000-0000-000000000002",
        "INTEGRITY": "a1000000-0000-0000-0000-000000000003",
        "SELF_CONFIDENCE": "a1000000-0000-0000-0000-000000000004",
        "FLEXIBILITY": "a1000000-0000-0000-0000-000000000005"
    }

    # Seed loop: generate 50 users
    for i in range(1, 51):
        user_uuid = str(uuid.uuid4())
        profile_uuid = str(uuid.uuid4())
        
        name = random.choice(first_names)
        surname = random.choice(surnames)
        email = f"seed.user{i}@{random.choice(['example.com', 'micache.com', 'talent.io'])}"
        
        # Determine professional sector for this user
        if i <= 20:
            sector_name = "IT_DEV"
            role = "ROLE_USER"
            sector_desc = "desarrollo y tecnologías de la información"
        elif i <= 35:
            sector_name = "IT_MGMT"
            role = "ROLE_USER"
            sector_desc = "gestión de productos y metodologías ágiles"
        else:
            sector_name = "NON_IT"
            role = "ROLE_USER"
            sector_desc = "operaciones, ventas y recursos humanos"
            
        job_title, dept, functions_desc = random.choice(sectors[sector_name])
        
        years_exp = random.randint(3, 12)
        about_me = random.choice(about_me_templates).format(years=years_exp, sector_desc=sector_desc)
        education = f"Grado en {dept}, {random.choice(universities)}"

        # Escape single quotes for SQL compatibility
        name_esc = name.replace("'", "''")
        surname_esc = surname.replace("'", "''")
        job_title_esc = job_title.replace("'", "''")
        dept_esc = dept.replace("'", "''")
        about_me_esc = about_me.replace("'", "''")
        education_esc = education.replace("'", "''")

        # User record
        out_sql.append(f"-- User {i}: {name_esc} {surname_esc} ({job_title_esc})")
        out_sql.append(
            f"INSERT INTO users (id, email, password_hash, provider, role, name, is_active) "
            f"VALUES ('{user_uuid}', '{email}', '{bcrypt_password_hash}', 'LOCAL', '{role}', '{name_esc} {surname_esc}', true);"
        )

        # Profile record
        city = random.choice(cities)
        phone = f"+34 6{random.randint(10000000, 99999999)}"
        years_exp = random.randint(3, 12)
        birthday = (datetime.now() - timedelta(days=365 * random.randint(25, 45))).strftime("%Y-%m-%d")
        zipcode = f"{random.randint(28001, 28080)}"
        
        out_sql.append(
            f"INSERT INTO user_profiles (id, user_id, name, surname, contact_email, about_me, city, birthday, zipcode, phone_number, job_title, education) "
            f"VALUES ('{profile_uuid}', '{user_uuid}', '{name_esc}', '{surname_esc}', '{email}', "
            f"'{about_me_esc}', '{city}', '{birthday}', '{zipcode}', '{phone}', '{job_title_esc}', '{education_esc}');"
        )

        # Generate Experiences (1 to 3 per user)
        num_exp = random.randint(1, 3)
        exp_list = []
        
        current_year = 2026
        # Generate experiences starting from oldest to newest
        for e in range(num_exp):
            exp_uuid = str(uuid.uuid4())
            comp = random.choice(companies)
            # Ensure unique company in this list
            while comp in [x[0] for x in exp_list]:
                comp = random.choice(companies)
                
            # Date planning
            if e == 0:  # Current job
                start_year = current_year - random.randint(1, 3)
                start_date = f"{start_year}-01-15"
                finish_date = "NULL"
                still_works = "true"
            else:  # Past jobs
                finish_year = current_year - (e * 2)
                start_year = finish_year - random.randint(1, 3)
                start_date = f"{start_year}-03-10"
                finish_date = f"'{finish_year}-12-20'"
                still_works = "false"
                
            past_job_title, past_dept, past_func = random.choice(sectors[sector_name])
            
            out_sql.append(
                f"INSERT INTO experiences (id, user_id, company_name, department, position, start_date, finish_date, functions) "
                f"VALUES ('{exp_uuid}', '{user_uuid}', '{comp}', '{past_dept}', '{past_job_title}', '{start_date}', {finish_date}, '{past_func}');"
            )
            exp_list.append((comp, exp_uuid, still_works))

        # Generate Feedback Requests (cache_requests) (2 to 4 per user)
        num_requests = random.randint(2, 4)
        for r in range(num_requests):
            req_uuid = str(uuid.uuid4())
            url_token = str(uuid.uuid4())
            
            # Select a random experience from this user's list
            comp, exp_id, still_works_exp = random.choice(exp_list)
            
            # Target details (who is asked for feedback)
            target_f = random.choice(first_names)
            target_s = random.choice(surnames)
            target_e = f"seed.evaluator{i}_{r}@{comp.lower().replace(' ', '')}.com"
            target_phone = f"+34 6{random.randint(10000000, 99999999)}"
            
            relationship_id = random.randint(0, 4) # 0 to 4: direct manager, colleague, subordinate, client, other
            finished = "true" if random.random() < 0.7 else "false" # 70% finished requests
            
            out_sql.append(
                f"INSERT INTO cache_requests (id, user_id, experience_id, relationship_id, still_works_there, target_name, target_surname, target_email, url_token, finished, target_phone, is_visible) "
                f"VALUES ('{req_uuid}', '{user_uuid}', '{exp_id}', {relationship_id}, {still_works_exp}, '{target_f}', '{target_s}', '{target_e}', '{url_token}', {finished}, '{target_phone}', true);"
            )
            
        out_sql.append("") # Spacer
        all_users.append(user_uuid)

    # 2. PL/SQL script to insert ratings for all completed feedback requests
    out_sql.append("/* ========================================================= */")
    out_sql.append("/* PL/SQL TO GENERATE ORGANISED RATINGS (FEEDBACK RESPONSES) */")
    out_sql.append("/* ========================================================= */")
    out_sql.append("""
DO $$
DECLARE
    req RECORD;
    q RECORD;
    score INT;
BEGIN
    -- Only loop over our newly seeded users' requests that are finished
    FOR req IN 
        SELECT cr.id, cr.user_id 
        FROM cache_requests cr 
        JOIN users u ON u.id = cr.user_id
        WHERE cr.finished = true AND u.email LIKE 'seed.%'
    LOOP
        FOR q IN SELECT id FROM skill_questions LOOP
            -- Generamos una puntuación lógica con sesgo alto (3 a 5), que es lo normal en valoraciones profesionales reales
            -- 3: 20%, 4: 45%, 5: 30%, 1-2: 5% para añadir ruido real
            DECLARE
                rand FLOAT := random();
            BEGIN
                IF rand < 0.02 THEN
                    score := 1;
                ELSIF rand < 0.05 THEN
                    score := 2;
                ELSIF rand < 0.25 THEN
                    score := 3;
                ELSIF rand < 0.70 THEN
                    score := 4;
                ELSE
                    score := 5;
                END IF;
            END;

            INSERT INTO feedback_responses (cache_request_id, question_id, rating)
            VALUES (req.id, q.id, score)
            ON CONFLICT (cache_request_id, question_id) DO NOTHING;
        END LOOP;
    END LOOP;
END $$;
""")

    # 3. PL/SQL script to recalculate and populate user_skills_metrics for each seeded user
    out_sql.append("/* ========================================================= */")
    out_sql.append("/* PL/SQL TO CALCULATE AND POPULATE USER SKILLS METRICS      */")
    out_sql.append("/* ========================================================= */")
    out_sql.append("""
DO $$
DECLARE
    usr RECORD;
    tw FLOAT;
    sc FLOAT;
    pr FLOAT;
    it FLOAT;
    fl FLOAT;
    avg_s FLOAT;
BEGIN
    FOR usr IN SELECT id FROM users WHERE email LIKE 'seed.%' LOOP
        -- TEAMWORK
        SELECT COALESCE(AVG(fr.rating), 0) INTO tw
        FROM feedback_responses fr
        JOIN skill_questions sq ON sq.id = fr.question_id
        JOIN skill_categories sc_cat ON sc_cat.id = sq.category_id
        JOIN cache_requests cr ON cr.id = fr.cache_request_id
        WHERE cr.user_id = usr.id AND cr.finished = true AND cr.is_visible = true
        AND sc_cat.code = 'TEAMWORK';

        -- SELF_CONFIDENCE
        SELECT COALESCE(AVG(fr.rating), 0) INTO sc
        FROM feedback_responses fr
        JOIN skill_questions sq ON sq.id = fr.question_id
        JOIN skill_categories sc_cat ON sc_cat.id = sq.category_id
        JOIN cache_requests cr ON cr.id = fr.cache_request_id
        WHERE cr.user_id = usr.id AND cr.finished = true AND cr.is_visible = true
        AND sc_cat.code = 'SELF_CONFIDENCE';

        -- PROACTIVITY
        SELECT COALESCE(AVG(fr.rating), 0) INTO pr
        FROM feedback_responses fr
        JOIN skill_questions sq ON sq.id = fr.question_id
        JOIN skill_categories sc_cat ON sc_cat.id = sq.category_id
        JOIN cache_requests cr ON cr.id = fr.cache_request_id
        WHERE cr.user_id = usr.id AND cr.finished = true AND cr.is_visible = true
        AND sc_cat.code = 'PROACTIVITY';

        -- INTEGRITY
        SELECT COALESCE(AVG(fr.rating), 0) INTO it
        FROM feedback_responses fr
        JOIN skill_questions sq ON sq.id = fr.question_id
        JOIN skill_categories sc_cat ON sc_cat.id = sq.category_id
        JOIN cache_requests cr ON cr.id = fr.cache_request_id
        WHERE cr.user_id = usr.id AND cr.finished = true AND cr.is_visible = true
        AND sc_cat.code = 'INTEGRITY';

        -- FLEXIBILITY
        SELECT COALESCE(AVG(fr.rating), 0) INTO fl
        FROM feedback_responses fr
        JOIN skill_questions sq ON sq.id = fr.question_id
        JOIN skill_categories sc_cat ON sc_cat.id = sq.category_id
        JOIN cache_requests cr ON cr.id = fr.cache_request_id
        WHERE cr.user_id = usr.id AND cr.finished = true AND cr.is_visible = true
        AND sc_cat.code = 'FLEXIBILITY';

        -- OVERALL AVERAGE
        SELECT COALESCE(AVG(fr.rating), 0) INTO avg_s
        FROM feedback_responses fr
        JOIN cache_requests cr ON cr.id = fr.cache_request_id
        WHERE cr.user_id = usr.id AND cr.finished = true AND cr.is_visible = true;

        -- Clean and insert fresh metrics
        DELETE FROM user_skills_metrics WHERE user_id = usr.id;
        
        -- Round metric averages to 2 decimals
        INSERT INTO user_skills_metrics (user_id, teamwork, self_confidence, proactivity, integrity, flexibility, average_score, created_at, updated_at)
        VALUES (
            usr.id, 
            ROUND(tw::numeric, 2), 
            ROUND(sc::numeric, 2), 
            ROUND(pr::numeric, 2), 
            ROUND(it::numeric, 2), 
            ROUND(fl::numeric, 2), 
            ROUND(avg_s::numeric, 2), 
            CURRENT_TIMESTAMP, 
            CURRENT_TIMESTAMP
        );
    END LOOP;
END $$;
""")

    out_sql.append("\nANALYZE users, user_profiles, experiences, cache_requests, feedback_responses, user_skills_metrics;")
    out_sql.append("SELECT '✅ SEED DATA POPULATED SUCCESSFULLY' AS status;")

    # Write SQL content to deploy/seed_data.sql
    with open("/home/tino/Projects/root-project-ia/deploy/seed_data.sql", "w", encoding="utf-8") as f:
        f.write("\n".join(out_sql))
    
    print("✨ Successfully generated deploy/seed_data.sql!")

if __name__ == "__main__":
    generate_seed()
