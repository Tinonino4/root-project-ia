/* =========================================================
   MODULE: BEHAVIORAL QUESTIONNAIRES (360° MODEL)
   ========================================================= */

CREATE TABLE IF NOT EXISTS behavioral_questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    relationship_type_id INT NOT NULL,
    question_code VARCHAR(50) NOT NULL,
    question_type VARCHAR(50) NOT NULL, -- 'BARS', 'FORCED_CHOICE', 'CULTURAL_FIT'
    question_text TEXT NOT NULL,
    position INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_bquestion_relationship FOREIGN KEY (relationship_type_id) REFERENCES relationship_types(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_bquestions_relationship ON behavioral_questions(relationship_type_id);

CREATE TABLE IF NOT EXISTS behavioral_question_options (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    question_id UUID NOT NULL,
    option_code VARCHAR(100) NOT NULL,
    option_text TEXT NOT NULL,
    teamwork_pts INT NOT NULL DEFAULT 0,
    proactivity_pts INT NOT NULL DEFAULT 0,
    flexibility_pts INT NOT NULL DEFAULT 0,
    integrity_pts INT NOT NULL DEFAULT 0,
    leadership_pts INT NOT NULL DEFAULT 0,
    position INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_boption_question FOREIGN KEY (question_id) REFERENCES behavioral_questions(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_boptions_question ON behavioral_question_options(question_id);

CREATE TABLE IF NOT EXISTS behavioral_responses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cache_request_id UUID NOT NULL,
    question_id UUID NOT NULL,
    selected_option_id UUID NOT NULL,
    extra_data JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_bresponse_request FOREIGN KEY (cache_request_id) REFERENCES cache_requests(id) ON DELETE CASCADE,
    CONSTRAINT fk_bresponse_question FOREIGN KEY (question_id) REFERENCES behavioral_questions(id) ON DELETE CASCADE,
    CONSTRAINT fk_bresponse_option FOREIGN KEY (selected_option_id) REFERENCES behavioral_question_options(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_bresponses_request ON behavioral_responses(cache_request_id);

/* =========================================================
   SEED DATA: CUESTIONARIOS CONDUCTUALES 360°
   ========================================================= */

-- ROL 1: COMPAÑEROS (COLLEAGUE - ID 1)
INSERT INTO behavioral_questions (id, relationship_type_id, question_code, question_type, question_text, position) VALUES
('b1000001-0000-0000-0000-000000000001', 1, 'Q1_1_CONFLICT', 'BARS', 'Ante un desacuerdo técnico o metodológico dentro del equipo, la actitud habitual es:', 1),
('b1000001-0000-0000-0000-000000000002', 1, 'Q1_2_SUPPORT', 'BARS', 'Cuando un compañero de equipo está desbordado con una entrega o tarea difícil:', 2),
('b1000001-0000-0000-0000-000000000003', 1, 'Q1_3_COMMUNICATION', 'BARS', 'En la dinámica de comunicación habitual del equipo (chats, reuniones, traspasos):', 3),
('b1000001-0000-0000-0000-000000000004', 1, 'Q1_4_FORCED_CHOICE', 'FORCED_CHOICE', 'Virtudes Destacadas (Selecciona exactamente las 2 más representativas):', 4),
('b1000001-0000-0000-0000-000000000005', 1, 'Q1_5_CULTURAL_FIT', 'CULTURAL_FIT', 'Entorno donde saca su MEJOR versión (Elige 1):', 5);

-- Opciones Q1.1 (Conflictos)
INSERT INTO behavioral_question_options (id, question_id, option_code, option_text, teamwork_pts, proactivity_pts, flexibility_pts, integrity_pts, leadership_pts, position) VALUES
('c1000001-0001-0000-0000-000000000001', 'b1000001-0000-0000-0000-000000000001', 'MEDIATOR', 'Mediador / Conciliador: Busca rebajar la tensión, encontrar puntos de encuentro y mantener la buena sintonía del grupo.', 95, 70, 90, 80, 75, 1),
('c1000001-0001-0000-0000-000000000002', 'b1000001-0000-0000-0000-000000000001', 'ANALYTICAL', 'Analítico / Orientado a Datos: Propone aislar las emociones, revisar datos de partida y elegir la solución objetivamente más eficiente.', 75, 85, 70, 95, 80, 2),
('c1000001-0001-0000-0000-000000000003', 'b1000001-0000-0000-0000-000000000001', 'DEFENDER', 'Defensor de Estándares: Defiende con firmeza las buenas prácticas y la calidad, asegurando que no se tomen atajos.', 70, 75, 65, 100, 80, 3),
('c1000001-0001-0000-0000-000000000004', 'b1000001-0000-0000-0000-000000000001', 'PRAGMATIC', 'Pragmático / Orientado a Tiempo: Aboga por tomar una decisión rápida para no bloquear la entrega del equipo.', 80, 95, 85, 75, 85, 4);

-- Opciones Q1.2 (Apoyo)
INSERT INTO behavioral_question_options (id, question_id, option_code, option_text, teamwork_pts, proactivity_pts, flexibility_pts, integrity_pts, leadership_pts, position) VALUES
('c1000001-0002-0000-0000-000000000001', 'b1000001-0000-0000-0000-000000000002', 'MENTOR', 'Mentor / Formador: Le acompaña explicándole cómo resolverlo por sí mismo para que aprenda hacia el futuro.', 95, 80, 80, 90, 90, 1),
('c1000001-0002-0000-0000-000000000002', 'b1000001-0000-0000-0000-000000000002', 'EXECUTION_HELP', 'Ejecutor / Alivio de Carga: Se ofrece a asumir directamente parte de las tareas del compañero para reducir la presión inmediata.', 100, 85, 90, 80, 75, 2),
('c1000001-0002-0000-0000-000000000003', 'b1000001-0000-0000-0000-000000000002', 'ENVIRONMENT_FACILITATOR', 'Facilitador de Entorno: Busca reordenar prioridades o hablar con el entorno para quitarle distracciones al compañero.', 85, 90, 85, 85, 85, 3),
('c1000001-0002-0000-0000-000000000004', 'b1000001-0000-0000-0000-000000000002', 'INDIVIDUAL_FOCUS', 'Foco Individual / Respetuoso: Confía en la capacidad del compañero y se asegura de que su propia parte esté impecable.', 65, 80, 65, 95, 70, 4);

-- Opciones Q1.3 (Comunicación)
INSERT INTO behavioral_question_options (id, question_id, option_code, option_text, teamwork_pts, proactivity_pts, flexibility_pts, integrity_pts, leadership_pts, position) VALUES
('c1000001-0003-0000-0000-000000000001', 'b1000001-0000-0000-0000-000000000003', 'SYNTHETIC', 'Sintético y Frecuente: Comunica estados de forma muy breve y continua. Mantiene el pulso en tiempo real.', 85, 90, 85, 80, 80, 1),
('c1000001-0003-0000-0000-000000000002', 'b1000001-0000-0000-0000-000000000003', 'STRUCTURED', 'Detallado y Estructurado: Prepara hilos o documentos completos con todo el contexto antes de abrir un debate.', 75, 75, 70, 100, 75, 2),
('c1000001-0003-0000-0000-000000000003', 'b1000001-0000-0000-0000-000000000003', 'SPONTANEOUS', 'Espontáneo y Relacional: Prefiere el contacto directo (llamada rápida o presencial) para resolver dudas en el momento.', 95, 85, 90, 75, 80, 3),
('c1000001-0003-0000-0000-000000000004', 'b1000001-0000-0000-0000-000000000003', 'MILESTONE_ORIENTED', 'Orientado a Hitos: Se comunica principalmente cuando hay avances significativos o bloqueos reales que reportar.', 70, 80, 75, 85, 75, 4);

-- Opciones Q1.4 (Elección Forzada - Pick 2 Virtudes)
INSERT INTO behavioral_question_options (id, question_id, option_code, option_text, teamwork_pts, proactivity_pts, flexibility_pts, integrity_pts, leadership_pts, position) VALUES
('c1000001-0004-0000-0000-000000000001', 'b1000001-0000-0000-0000-000000000004', 'GOOD_HUMOR', 'Genera un clima de confianza y buen humor en el equipo incluso bajo presión.', 95, 75, 90, 85, 80, 1),
('c1000001-0004-0000-0000-000000000002', 'b1000001-0000-0000-0000-000000000004', 'TECH_BENCHMARK', 'Es una referencia técnica/profesional a la que acudir cuando algo se complica.', 80, 90, 80, 90, 95, 2),
('c1000001-0004-0000-0000-000000000003', 'b1000001-0000-0000-0000-000000000004', 'QUALITY_RIGOR', 'Es extremadamente riguroso/a con la calidad del producto final.', 70, 75, 70, 100, 80, 3),
('c1000001-0004-0000-0000-000000000004', 'b1000001-0000-0000-0000-000000000004', 'HIGH_OUTPUT', 'Tiene una capacidad de trabajo y volumen de ejecución muy alto.', 75, 100, 80, 85, 85, 4),
('c1000001-0004-0000-0000-000000000005', 'b1000001-0000-0000-0000-000000000004', 'ADAPTABILTY', 'Se adapta sin quejarse cuando cambian las reglas del juego a mitad de camino.', 85, 80, 100, 80, 75, 5);

-- Opciones Q1.5 (Fit Cultural)
INSERT INTO behavioral_question_options (id, question_id, option_code, option_text, teamwork_pts, proactivity_pts, flexibility_pts, integrity_pts, leadership_pts, position) VALUES
('c1000001-0005-0000-0000-000000000001', 'b1000001-0000-0000-0000-000000000005', 'MULTIDISCIPLINARY_AGILE', 'Equipos multidisciplinares muy unidos que trabajan en constante lluvia de ideas.', 95, 85, 90, 80, 85, 1),
('c1000001-0005-0000-0000-000000000002', 'b1000001-0000-0000-0000-000000000005', 'AUTONOMOUS_SPECIALISTS', 'Equipos autónomos donde cada especialista tiene su área de responsabilidad clara.', 75, 95, 75, 90, 85, 2),
('c1000001-0005-0000-0000-000000000003', 'b1000001-0000-0000-0000-000000000005', 'HIGH_TECH_EXCELLENCE', 'Equipos altamente técnicos donde se premia la excelencia y el rigor por encima de la velocidad.', 75, 80, 70, 100, 90, 3);


-- ROL 2: JEFES / MÁNAGERS (DIRECT_MANAGER - ID 0)
INSERT INTO behavioral_questions (id, relationship_type_id, question_code, question_type, question_text, position) VALUES
('b2000001-0000-0000-0000-000000000001', 0, 'Q2_1_AUTONOMY', 'BARS', 'Cuando le asignas un nuevo objetivo con cierto grado de ambigüedad:', 1),
('b2000001-0000-0000-0000-000000000002', 0, 'Q2_2_FEEDBACK', 'BARS', 'Cuando le transmites correcciones, cambios de rumbo o feedback sobre su trabajo:', 2),
('b2000001-0000-0000-0000-000000000003', 0, 'Q2_3_RISK_ERRORS', 'BARS', 'Si durante la ejecución de una tarea surge un imprevisto o cometió un error:', 3),
('b2000001-0000-0000-0000-000000000004', 0, 'Q2_4_FORCED_CHOICE', 'FORCED_CHOICE', 'Virtudes Destacadas para un Mánager (Selecciona exactamente las 2 más representativas):', 4),
('b2000001-0000-0000-0000-000000000005', 0, 'Q2_5_CULTURAL_FIT', 'CULTURAL_FIT', 'Estilo de Gestión que Maximiza su Rendimiento (Elige 1):', 5);

-- Opciones Q2.1 (Autonomía)
INSERT INTO behavioral_question_options (id, question_id, option_code, option_text, teamwork_pts, proactivity_pts, flexibility_pts, integrity_pts, leadership_pts, position) VALUES
('c2000001-0001-0000-0000-000000000001', 'b2000001-0000-0000-0000-000000000001', 'EXPLORER', 'Explorador Autónomo: Investiga por su cuenta, define el camino y presenta una propuesta completa para su validación.', 75, 100, 85, 85, 90, 1),
('c2000001-0001-0000-0000-000000000002', 'b2000001-0000-0000-0000-000000000001', 'ALIGNER', 'Alineador Frecuente: Define las primeras etapas y consulta periódicamente para asegurarse de ir exactamente en la dirección deseada.', 90, 75, 80, 90, 75, 2),
('c2000001-0001-0000-0000-000000000003', 'b2000001-0000-0000-0000-000000000001', 'STRUCTURED_PLANNER', 'Planificador Estructurado: Pide definir primero los criterios de éxito y límites antes de empezar a trazar la estrategia.', 75, 70, 70, 95, 80, 3),
('c2000001-0001-0000-0000-000000000004', 'b2000001-0000-0000-0000-000000000001', 'FAST_EXECUTOR', 'Ejecutor Rápido: Empieza a construir de inmediato un prototipo o borrador rápido para iterar sobre algo tangible.', 80, 95, 90, 75, 85, 4);

-- Opciones Q2.2 (Feedback)
INSERT INTO behavioral_question_options (id, question_id, option_code, option_text, teamwork_pts, proactivity_pts, flexibility_pts, integrity_pts, leadership_pts, position) VALUES
('c2000001-0002-0000-0000-000000000001', 'b2000001-0000-0000-0000-000000000002', 'REFLECTIVE', 'Reflexivo / Procesador: Escucha en silencio, procesa la información y aplica los ajustes tras meditarlo.', 75, 75, 80, 95, 75, 1),
('c2000001-0002-0000-0000-000000000002', 'b2000001-0000-0000-0000-000000000002', 'CONTRASTER', 'Discutidor / Contrastador: Debate y hace preguntas incisivas para entender la motivación profunda antes de modificar su trabajo.', 70, 85, 70, 90, 85, 2),
('c2000001-0002-0000-0000-000000000003', 'b2000001-0000-0000-0000-000000000002', 'AGILE_ACTION', 'Ágil / Orientado a Acción: Toma nota de inmediato y aplica los cambios solicitados con máxima celeridad.', 85, 90, 95, 85, 80, 3),
('c2000001-0002-0000-0000-000000000004', 'b2000001-0000-0000-0000-000000000002', 'PROPOSER', 'Propositivo / Alternativo: Acepta el feedback pero suele sugerir una tercera vía que no se había considerado.', 80, 95, 85, 85, 90, 4);

-- Opciones Q2.3 (Gestión de Riesgo)
INSERT INTO behavioral_question_options (id, question_id, option_code, option_text, teamwork_pts, proactivity_pts, flexibility_pts, integrity_pts, leadership_pts, position) VALUES
('c2000001-0003-0000-0000-000000000001', 'b2000001-0000-0000-0000-000000000003', 'IMMEDIATE_TRANSPARENCY', 'Transparencia Inmediata: Lo comunica al mánager en el instante en que ocurre, antes incluso de tener la solución.', 85, 75, 80, 100, 75, 1),
('c2000001-0003-0000-0000-000000000002', 'b2000001-0000-0000-0000-000000000003', 'SOLUTION_PRESENTED', 'Solución Presentada: Investiga cómo arreglarlo y comunica el error junto con la solución ya ejecutada o lista.', 80, 95, 85, 90, 90, 2),
('c2000001-0003-0000-0000-000000000003', 'b2000001-0000-0000-0000-000000000003', 'SELF_CONTAINMENT', 'Autocontención: Trabaja intensamente en resolverlo por su cuenta para minimizar el impacto antes de escalar.', 65, 90, 75, 80, 75, 3),
('c2000001-0003-0000-0000-000000000004', 'b2000001-0000-0000-0000-000000000003', 'ROOT_CAUSE_ANALYSIS', 'Análisis de Causa Raíz: Se enfoca en documentar por qué ocurrió para que el equipo no vuelva a cometer el mismo fallo.', 80, 80, 70, 95, 85, 4);

-- Opciones Q2.4 (Elección Forzada - Pick 2 Virtudes)
INSERT INTO behavioral_question_options (id, question_id, option_code, option_text, teamwork_pts, proactivity_pts, flexibility_pts, integrity_pts, leadership_pts, position) VALUES
('c2000001-0004-0000-0000-000000000001', 'b2000001-0000-0000-0000-000000000004', 'DELEGATE_CONFIDENCE', 'Puedes delegarle un tema complejo y despreocuparte porque sabes que saldrá bien.', 85, 95, 85, 95, 90, 1),
('c2000001-0004-0000-0000-000000000002', 'b2000001-0000-0000-0000-000000000004', 'INNOVATION', 'Aporta ideas innovadoras que mejoran la eficiencia del departamento.', 75, 100, 85, 80, 90, 2),
('c2000001-0004-0000-0000-000000000003', 'b2000001-0000-0000-0000-000000000004', 'EMOTIONAL_MATURITY', 'Tiene una madurez emocional excelente para encajar picos de estrés o presión.', 80, 80, 100, 90, 85, 3),
('c2000001-0004-0000-0000-000000000004', 'b2000001-0000-0000-0000-000000000004', 'PROCESS_DISCIPLINE', 'Es muy disciplinado/a con los procesos y estándares de la empresa.', 70, 75, 65, 100, 75, 4),
('c2000001-0004-0000-0000-000000000005', 'b2000001-0000-0000-0000-000000000004', 'VERSATILITY', 'Es capaz de asumir roles o tareas fuera de su descripción de puesto si la empresa lo necesita.', 85, 90, 95, 85, 80, 5);

-- Opciones Q2.5 (Fit Cultural)
INSERT INTO behavioral_question_options (id, question_id, option_code, option_text, teamwork_pts, proactivity_pts, flexibility_pts, integrity_pts, leadership_pts, position) VALUES
('c2000001-0005-0000-0000-000000000001', 'b2000001-0000-0000-0000-000000000005', 'HANDS_OFF', 'Liderazgo Hands-off: Darle libertad total de agenda y evaluar únicamente los resultados finales.', 70, 100, 80, 85, 90, 1),
('c2000001-0005-0000-0000-000000000002', 'b2000001-0000-0000-0000-000000000005', 'COACH_LEADERSHIP', 'Liderazgo Coach: Acompañamiento cercano, retos intelectuales y feedback continuo.', 90, 85, 85, 90, 85, 2),
('c2000001-0005-0000-0000-000000000003', 'b2000001-0000-0000-0000-000000000005', 'DIRECTIVE_LEADERSHIP', 'Liderazgo Directivo: Metas muy acotadas, procesos claros y seguimiento frecuente.', 75, 70, 65, 95, 75, 3);


-- ROL 3: SUBORDINADOS / EQUIPO (SUBORDINATE - ID 2)
INSERT INTO behavioral_questions (id, relationship_type_id, question_code, question_type, question_text, position) VALUES
('b3000001-0000-0000-0000-000000000001', 2, 'Q3_1_DELEGATION', 'BARS', 'Al asignarte responsabilidades y proyectos como miembro de su equipo:', 1),
('b3000001-0000-0000-0000-000000000002', 2, 'Q3_2_PRESSURE', 'BARS', 'Cuando el equipo recibe exigencias fuertes de dirección, clientes o plazos ajustados:', 2),
('b3000001-0000-0000-0000-000000000003', 2, 'Q3_3_DEVELOPMENT', 'BARS', 'Respecto a tu evolución y desempeño como parte de su equipo:', 3),
('b3000001-0000-0000-0000-000000000004', 2, 'Q3_4_FORCED_CHOICE', 'FORCED_CHOICE', 'Atributos de Liderazgo Top (Selecciona exactamente los 2 más representativos):', 4),
('b3000001-0000-0000-0000-000000000005', 2, 'Q3_5_CULTURAL_FIT', 'CULTURAL_FIT', '¿Para qué tipo de equipo es el LÍDER IDEAL? (Elige 1):', 5);

-- Opciones Q3.1 (Delegación)
INSERT INTO behavioral_question_options (id, question_id, option_code, option_text, teamwork_pts, proactivity_pts, flexibility_pts, integrity_pts, leadership_pts, position) VALUES
('c3000001-0001-0000-0000-000000000001', 'b3000001-0000-0000-0000-000000000001', 'TRUST_DELEGATION', 'Delegación por Confianza: Te entrega el objetivo final y te da libertad total para decidir cómo alcanzarlo.', 80, 95, 85, 90, 95, 1),
('c3000001-0001-0000-0000-000000000002', 'b3000001-0000-0000-0000-000000000001', 'STRUCTURED_DELEGATION', 'Delegación Estructurada: Te entrega la tarea junto con pautas, herramientas y metodologías a utilizar.', 75, 75, 70, 95, 80, 2),
('c3000001-0001-0000-0000-000000000003', 'b3000001-0000-0000-0000-000000000001', 'COLLABORATIVE_DELEGATION', 'Delegación Colaborativa: Sienta las bases contigo en una sesión conjunta y trabaja hombro con hombro al inicio.', 95, 85, 85, 90, 85, 3),
('c3000001-0001-0000-0000-000000000004', 'b3000001-0000-0000-0000-000000000001', 'MILESTONE_DELEGATION', 'Delegación por Hitos: Te asigna fases cortas y va liberando más responsabilidad conforme se superan.', 80, 80, 80, 90, 85, 4);

-- Opciones Q3.2 (Presión)
INSERT INTO behavioral_question_options (id, question_id, option_code, option_text, teamwork_pts, proactivity_pts, flexibility_pts, integrity_pts, leadership_pts, position) VALUES
('c3000001-0002-0000-0000-000000000001', 'b3000001-0000-0000-0000-000000000002', 'SHIELD_PROTECTOR', 'Escudo Protector: Filtra la tensión hacia el equipo, absorbe la presión e intenta renegociar plazos fuera.', 90, 85, 95, 90, 95, 1),
('c3000001-0002-0000-0000-000000000002', 'b3000001-0000-0000-0000-000000000002', 'TRANSPARENT_TRANSMITTER', 'Transmisor Transparente: Explica al equipo con total honestidad la urgencia de la situación para remar juntos.', 95, 85, 80, 100, 85, 2),
('c3000001-0002-0000-0000-000000000003', 'b3000001-0000-0000-0000-000000000002', 'PRAGMATIC_PRIORITIZER', 'Priorizador Pragmático: Decide qué tareas secundarias se dejan de hacer para cumplir lo urgente sin quemar al equipo.', 85, 95, 90, 85, 90, 3),
('c3000001-0002-0000-0000-000000000004', 'b3000001-0000-0000-0000-000000000002', 'TRENCH_LEADER', 'Líder de Trinchera: Se pone a trabajar al mismo nivel que el equipo para sacar el trabajo a tiempo.', 100, 90, 90, 90, 85, 4);

-- Opciones Q3.3 (Desarrollo y Feedback)
INSERT INTO behavioral_question_options (id, question_id, option_code, option_text, teamwork_pts, proactivity_pts, flexibility_pts, integrity_pts, leadership_pts, position) VALUES
('c3000001-0003-0000-0000-000000000001', 'b3000001-0000-0000-0000-000000000003', 'CONTINUOUS_FEEDBACK', 'Feedback Continuo y En el Momento: Te señala aciertos y mejoras inmediatamente después de que sucedan.', 85, 90, 85, 95, 90, 1),
('c3000001-0003-0000-0000-000000000002', 'b3000001-0000-0000-0000-000000000003', 'CAREER_MENTOR', 'Orientador de Carrera / Mentor: Se enfoca en tus metas a largo plazo y competencias que necesitas adquirir.', 90, 85, 80, 90, 95, 2),
('c3000001-0003-0000-0000-000000000003', 'b3000001-0000-0000-0000-000000000003', 'PUBLIC_RECOGNITION', 'Reconocimiento Público: Se asegura de dar visibilidad a tus logros ante las instancias superiores.', 95, 80, 85, 90, 90, 3),
('c3000001-0003-0000-0000-000000000004', 'b3000001-0000-0000-0000-000000000003', 'PRACTICAL_ENABLER', 'Enfoque Práctico: Te da herramientas y recursos concretos cuando ve que estás atascado.', 85, 85, 80, 90, 85, 4);

-- Opciones Q3.4 (Elección Forzada - Pick 2 Virtudes Liderazgo)
INSERT INTO behavioral_question_options (id, question_id, option_code, option_text, teamwork_pts, proactivity_pts, flexibility_pts, integrity_pts, leadership_pts, position) VALUES
('c3000001-0004-0000-0000-000000000001', 'b3000001-0000-0000-0000-000000000004', 'ACTIVE_LISTENING', 'Escucha activa y accesibilidad: siempre tiene la puerta abierta para su equipo.', 95, 80, 90, 90, 90, 1),
('c3000001-0004-0000-0000-000000000002', 'b3000001-0000-0000-0000-000000000004', 'STRATEGIC_CLARITY', 'Claridad estratégica: sabe hacia dónde va el equipo y lo transmite con pasión.', 80, 95, 80, 90, 100, 2),
('c3000001-0004-0000-0000-000000000003', 'b3000001-0000-0000-0000-000000000004', 'JUSTICE_INTEGRITY', 'Justicia e integridad: trata a todo el mundo con equidad y asume la culpa si el equipo falla.', 90, 80, 85, 100, 90, 3),
('c3000001-0004-0000-0000-000000000004', 'b3000001-0000-0000-0000-000000000004', 'TALENT_DEVELOPER', 'Capacidad para sacar la mejor versión de cada persona según su talento.', 95, 90, 85, 90, 95, 4),
('c3000001-0004-0000-0000-000000000005', 'b3000001-0000-0000-0000-000000000004', 'IMPECCABLE_CRITERIA', 'Criterio técnico / profesional impecable para guiar las decisiones del equipo.', 75, 90, 75, 95, 95, 5);

-- Opciones Q3.5 (Fit Cultural Liderazgo)
INSERT INTO behavioral_question_options (id, question_id, option_code, option_text, teamwork_pts, proactivity_pts, flexibility_pts, integrity_pts, leadership_pts, position) VALUES
('c3000001-0005-0000-0000-000000000001', 'b3000001-0000-0000-0000-000000000005', 'JUNIOR_GROWTH_TEAM', 'Equipos junior o en formación que necesitan guía, estructura y aprendizaje constante.', 90, 85, 80, 90, 95, 1),
('c3000001-0005-0000-0000-000000000002', 'b3000001-0000-0000-0000-000000000005', 'SENIOR_AUTONOMOUS_TEAM', 'Equipos senior con alta experiencia que necesitan autonomía y eliminación de obstáculos.', 80, 95, 90, 90, 95, 2),
('c3000001-0005-0000-0000-000000000003', 'b3000001-0000-0000-0000-000000000005', 'CRISIS_TRANSFORMATION_TEAM', 'Equipos en situaciones de crisis o transformación que necesitan rumbo firme y decisiones rápidas.', 80, 95, 95, 90, 100, 3);
