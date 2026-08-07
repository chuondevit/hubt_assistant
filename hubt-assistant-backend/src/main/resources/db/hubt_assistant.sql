
-- ============================================================
-- HUBT ASSISTANT - FULL POSTGRESQL DATABASE
-- PostgreSQL 15+
-- Updated for Spring Boot/JPA authentication module
-- ============================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS citext;

CREATE SCHEMA IF NOT EXISTS hubt;
SET search_path TO hubt, public;

-- ============================================================
-- ENUM TYPES
-- ============================================================

CREATE TYPE university_status AS ENUM (
    'ACTIVE', 'INACTIVE'
);

CREATE TYPE role_scope AS ENUM (
    'SYSTEM', 'UNIVERSITY', 'SELF'
);

CREATE TYPE auth_provider AS ENUM (
    'LOCAL', 'GOOGLE', 'FACEBOOK'
);

CREATE TYPE relationship_type AS ENUM (
    'FATHER', 'MOTHER', 'GUARDIAN', 'OTHER'
);

CREATE TYPE verification_status AS ENUM (
    'PENDING', 'VERIFIED', 'REJECTED'
);

CREATE TYPE online_status AS ENUM (
    'ONLINE', 'OFFLINE', 'AWAY'
);

CREATE TYPE availability_status AS ENUM (
    'AVAILABLE', 'BUSY', 'ON_LEAVE', 'UNAVAILABLE'
);

CREATE TYPE degree_level AS ENUM (
    'COLLEGE', 'BACHELOR', 'MASTER', 'DOCTOR'
);

CREATE TYPE admission_year_status AS ENUM (
    'DRAFT', 'OPEN', 'CLOSED', 'ARCHIVED'
);

CREATE TYPE generic_status AS ENUM (
    'ACTIVE', 'INACTIVE'
);

CREATE TYPE test_type AS ENUM (
    'DISC', 'HOLLAND', 'INTEREST', 'CAREER_ORIENTATION', 'ACADEMIC'
);

CREATE TYPE test_status AS ENUM (
    'DRAFT', 'PUBLISHED', 'ARCHIVED'
);

CREATE TYPE question_type AS ENUM (
    'SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'SCALE', 'TEXT', 'RANKING'
);

CREATE TYPE attempt_status AS ENUM (
    'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'EXPIRED'
);

CREATE TYPE interest_level AS ENUM (
    'LOW', 'MEDIUM', 'HIGH', 'VERY_HIGH'
);

CREATE TYPE recommendation_algorithm_type AS ENUM (
    'RULE_BASED', 'WEIGHTED_SCORING', 'MACHINE_LEARNING', 'HYBRID'
);

CREATE TYPE application_status AS ENUM (
    'DRAFT',
    'SUBMITTED',
    'UNDER_REVIEW',
    'NEED_ADDITIONAL_DOCUMENTS',
    'ELIGIBLE',
    'INELIGIBLE',
    'ADMITTED',
    'NOT_ADMITTED',
    'CONFIRMED',
    'ENROLLED',
    'WITHDRAWN'
);

CREATE TYPE document_type AS ENUM (
    'IDENTITY_CARD',
    'TRANSCRIPT',
    'GRADUATION_CERTIFICATE',
    'PRIORITY_CERTIFICATE',
    'PORTRAIT_PHOTO',
    'OTHER'
);

CREATE TYPE chat_session_type AS ENUM (
    'AI_ONLY', 'HUMAN_ONLY', 'HYBRID'
);

CREATE TYPE chat_session_status AS ENUM (
    'OPEN', 'WAITING_COUNSELOR', 'ASSIGNED', 'CLOSED', 'CANCELLED'
);

CREATE TYPE chat_sender_type AS ENUM (
    'CANDIDATE', 'PARENT', 'AI', 'COUNSELOR', 'SYSTEM'
);

CREATE TYPE handoff_reason AS ENUM (
    'USER_REQUEST',
    'LOW_CONFIDENCE',
    'SENSITIVE_QUESTION',
    'COMPLAINT',
    'APPLICATION_PROBLEM',
    'NO_KNOWLEDGE_RESULT'
);

CREATE TYPE queue_status AS ENUM (
    'WAITING', 'ASSIGNED', 'RESOLVED', 'CANCELLED'
);

CREATE TYPE appointment_status AS ENUM (
    'REQUESTED', 'CONFIRMED', 'COMPLETED', 'CANCELLED', 'NO_SHOW'
);

CREATE TYPE appointment_type AS ENUM (
    'ONLINE', 'OFFLINE'
);

CREATE TYPE notification_channel AS ENUM (
    'IN_APP', 'EMAIL', 'SMS', 'PUSH'
);

CREATE TYPE notification_status AS ENUM (
    'PENDING', 'SENT', 'FAILED', 'READ'
);

CREATE TYPE post_status AS ENUM (
    'DRAFT', 'PUBLISHED', 'ARCHIVED'
);

CREATE TYPE analytics_event_type AS ENUM (
    'PAGE_VIEW',
    'MAJOR_VIEW',
    'TEST_STARTED',
    'TEST_COMPLETED',
    'RECOMMENDATION_GENERATED',
    'RECOMMENDATION_VIEWED',
    'CHAT_STARTED',
    'COUNSELOR_REQUESTED',
    'APPLICATION_STARTED',
    'APPLICATION_SUBMITTED',
    'APPLICATION_CONFIRMED'
);

-- ============================================================
-- COMMON TRIGGER FUNCTION
-- ============================================================

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- A. UNIVERSITY, USER, ROLE, PERMISSION
-- ============================================================

CREATE TABLE universities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    short_name VARCHAR(100),
    description TEXT,
    address TEXT,
    email CITEXT,
    phone VARCHAR(30),
    website VARCHAR(255),
    logo_url TEXT,
    status university_status NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    university_id UUID REFERENCES universities(id),
    email CITEXT NOT NULL UNIQUE,
    phone VARCHAR(30) UNIQUE,
    password_hash TEXT,
    full_name VARCHAR(255) NOT NULL,
    avatar_url TEXT,
    date_of_birth DATE,
    gender VARCHAR(30) DEFAULT 'UNDISCLOSED'
        CHECK (
            gender IS NULL
            OR gender IN ('MALE', 'FEMALE', 'OTHER', 'UNDISCLOSED')
        ),
    account_status VARCHAR(30) NOT NULL DEFAULT 'PENDING'
        CHECK (
            account_status IN ('PENDING', 'ACTIVE', 'LOCKED', 'SUSPENDED', 'DELETED')
        ),
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    phone_verified BOOLEAN NOT NULL DEFAULT FALSE,
    last_login_at TIMESTAMPTZ,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ
);

CREATE TABLE user_identities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    provider auth_provider NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    provider_email CITEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(provider, provider_user_id)
);

CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    priority INT NOT NULL CHECK (priority >= 0 AND priority <= 100),
    scope role_scope NOT NULL,
    system_role BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(120) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    module VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE role_permissions (
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE user_roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    university_id UUID REFERENCES universities(id),
    assigned_by UUID REFERENCES users(id),
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expired_at TIMESTAMPTZ,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE(user_id, role_id, university_id)
);

CREATE TABLE candidate_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    candidate_code VARCHAR(50) UNIQUE,
    identity_number VARCHAR(50) UNIQUE,
    school_name VARCHAR(255),
    province_code VARCHAR(30),
    district_code VARCHAR(30),
    graduation_year INT CHECK (graduation_year BETWEEN 1990 AND 2100),
    education_level VARCHAR(100),
    career_goal TEXT,
    preferred_study_location VARCHAR(255),
    profile_completion_percent NUMERIC(5,2) NOT NULL DEFAULT 0
        CHECK (profile_completion_percent BETWEEN 0 AND 100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE parent_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    occupation VARCHAR(255),
    workplace VARCHAR(255),
    contact_address TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE parent_candidate_links (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    parent_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    candidate_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    relationship relationship_type NOT NULL,
    verification_status verification_status NOT NULL DEFAULT 'PENDING',
    approved_by_candidate BOOLEAN NOT NULL DEFAULT FALSE,
    approved_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(parent_user_id, candidate_user_id)
);

CREATE TABLE counselor_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    employee_code VARCHAR(50) UNIQUE,
    department VARCHAR(255),
    specialization TEXT,
    years_of_experience INT NOT NULL DEFAULT 0 CHECK (years_of_experience >= 0),
    max_concurrent_sessions INT NOT NULL DEFAULT 3 CHECK (max_concurrent_sessions > 0),
    rating_average NUMERIC(3,2) NOT NULL DEFAULT 0 CHECK (rating_average BETWEEN 0 AND 5),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE counselor_presence (
    counselor_id UUID PRIMARY KEY REFERENCES counselor_profiles(id) ON DELETE CASCADE,
    online_status online_status NOT NULL DEFAULT 'OFFLINE',
    availability_status availability_status NOT NULL DEFAULT 'UNAVAILABLE',
    current_session_count INT NOT NULL DEFAULT 0 CHECK (current_session_count >= 0),
    last_seen_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- B. UNIVERSITY ACADEMIC AND ADMISSION DATA
-- ============================================================

CREATE TABLE faculties (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    university_id UUID NOT NULL REFERENCES universities(id) ON DELETE CASCADE,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    dean_name VARCHAR(255),
    status generic_status NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(university_id, code)
);

CREATE TABLE majors (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    university_id UUID NOT NULL REFERENCES universities(id) ON DELETE CASCADE,
    faculty_id UUID REFERENCES faculties(id),
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    degree_level degree_level NOT NULL DEFAULT 'BACHELOR',
    duration_years NUMERIC(3,1) CHECK (duration_years > 0),
    description TEXT,
    learning_outcomes TEXT,
    career_opportunities TEXT,
    required_skills TEXT,
    thumbnail_url TEXT,
    status generic_status NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ,
    UNIQUE(university_id, code)
);

CREATE TABLE programs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    major_id UUID NOT NULL REFERENCES majors(id) ON DELETE CASCADE,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    training_mode VARCHAR(100),
    language VARCHAR(100),
    duration_years NUMERIC(3,1) CHECK (duration_years > 0),
    description TEXT,
    status generic_status NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(major_id, code)
);

CREATE TABLE admission_years (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    university_id UUID NOT NULL REFERENCES universities(id) ON DELETE CASCADE,
    year INT NOT NULL CHECK (year BETWEEN 2000 AND 2100),
    name VARCHAR(255),
    start_date DATE,
    end_date DATE,
    status admission_year_status NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(university_id, year),
    CHECK (end_date IS NULL OR start_date IS NULL OR end_date >= start_date)
);

CREATE TABLE admission_rounds (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    admission_year_id UUID NOT NULL REFERENCES admission_years(id) ON DELETE CASCADE,
    round_number INT NOT NULL CHECK (round_number > 0),
    name VARCHAR(255) NOT NULL,
    application_start_at TIMESTAMPTZ,
    application_end_at TIMESTAMPTZ,
    result_date DATE,
    confirmation_deadline TIMESTAMPTZ,
    status generic_status NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(admission_year_id, round_number),
    CHECK (
        application_end_at IS NULL
        OR application_start_at IS NULL
        OR application_end_at >= application_start_at
    )
);

CREATE TABLE admission_methods (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    university_id UUID NOT NULL REFERENCES universities(id) ON DELETE CASCADE,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status generic_status NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(university_id, code)
);

CREATE TABLE subjects (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE subject_combos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE subject_combo_items (
    subject_combo_id UUID NOT NULL REFERENCES subject_combos(id) ON DELETE CASCADE,
    subject_id UUID NOT NULL REFERENCES subjects(id) ON DELETE CASCADE,
    coefficient NUMERIC(4,2) NOT NULL DEFAULT 1 CHECK (coefficient > 0),
    PRIMARY KEY (subject_combo_id, subject_id)
);

CREATE TABLE major_admission_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    admission_year_id UUID NOT NULL REFERENCES admission_years(id) ON DELETE CASCADE,
    major_id UUID NOT NULL REFERENCES majors(id) ON DELETE CASCADE,
    program_id UUID REFERENCES programs(id) ON DELETE SET NULL,
    total_quota INT NOT NULL CHECK (total_quota >= 0),
    tuition_fee NUMERIC(15,2) CHECK (tuition_fee >= 0),
    expected_cutoff NUMERIC(5,2),
    application_open BOOLEAN NOT NULL DEFAULT TRUE,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX uq_major_admission_plan
ON major_admission_plans (
    admission_year_id,
    major_id,
    COALESCE(program_id, '00000000-0000-0000-0000-000000000000'::uuid)
);

CREATE TABLE major_admission_methods (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    major_admission_plan_id UUID NOT NULL REFERENCES major_admission_plans(id) ON DELETE CASCADE,
    admission_method_id UUID NOT NULL REFERENCES admission_methods(id),
    quota INT CHECK (quota >= 0),
    minimum_score NUMERIC(5,2),
    conditions_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    status generic_status NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(major_admission_plan_id, admission_method_id)
);

CREATE TABLE major_subject_combos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    major_admission_method_id UUID NOT NULL REFERENCES major_admission_methods(id) ON DELETE CASCADE,
    subject_combo_id UUID NOT NULL REFERENCES subject_combos(id),
    minimum_score NUMERIC(5,2),
    status generic_status NOT NULL DEFAULT 'ACTIVE',
    UNIQUE(major_admission_method_id, subject_combo_id)
);

CREATE TABLE major_cutoffs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    admission_year_id UUID NOT NULL REFERENCES admission_years(id) ON DELETE CASCADE,
    admission_round_id UUID REFERENCES admission_rounds(id) ON DELETE SET NULL,
    major_id UUID NOT NULL REFERENCES majors(id) ON DELETE CASCADE,
    admission_method_id UUID NOT NULL REFERENCES admission_methods(id),
    subject_combo_id UUID REFERENCES subject_combos(id),
    cutoff_score NUMERIC(5,2) NOT NULL CHECK (cutoff_score >= 0),
    published_at TIMESTAMPTZ,
    status generic_status NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- C. TEST, DISC, AI RECOMMENDATION
-- ============================================================

CREATE TABLE tests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    university_id UUID REFERENCES universities(id) ON DELETE CASCADE,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    test_type test_type NOT NULL,
    version INT NOT NULL DEFAULT 1 CHECK (version > 0),
    description TEXT,
    scoring_method VARCHAR(255),
    duration_minutes INT CHECK (duration_minutes > 0),
    status test_status NOT NULL DEFAULT 'DRAFT',
    published_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(university_id, code, version)
);

CREATE TABLE test_questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    test_id UUID NOT NULL REFERENCES tests(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    question_type question_type NOT NULL,
    display_order INT NOT NULL CHECK (display_order > 0),
    required BOOLEAN NOT NULL DEFAULT TRUE,
    status generic_status NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(test_id, display_order)
);

CREATE TABLE test_choices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    question_id UUID NOT NULL REFERENCES test_questions(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    value NUMERIC(8,2),
    dimension_code VARCHAR(20),
    display_order INT NOT NULL CHECK (display_order > 0),
    UNIQUE(question_id, display_order)
);

CREATE TABLE test_attempts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    test_id UUID NOT NULL REFERENCES tests(id),
    test_version INT NOT NULL,
    started_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    submitted_at TIMESTAMPTZ,
    status attempt_status NOT NULL DEFAULT 'IN_PROGRESS',
    total_score NUMERIC(10,2),
    completion_percent NUMERIC(5,2) NOT NULL DEFAULT 0
        CHECK (completion_percent BETWEEN 0 AND 100)
);

CREATE TABLE test_attempt_answers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    attempt_id UUID NOT NULL REFERENCES test_attempts(id) ON DELETE CASCADE,
    question_id UUID NOT NULL REFERENCES test_questions(id),
    choice_id UUID REFERENCES test_choices(id),
    raw_value TEXT,
    answered_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(attempt_id, question_id)
);

CREATE TABLE disc_scores (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    attempt_id UUID NOT NULL UNIQUE REFERENCES test_attempts(id) ON DELETE CASCADE,
    d_score NUMERIC(5,2) NOT NULL CHECK (d_score BETWEEN 0 AND 100),
    i_score NUMERIC(5,2) NOT NULL CHECK (i_score BETWEEN 0 AND 100),
    s_score NUMERIC(5,2) NOT NULL CHECK (s_score BETWEEN 0 AND 100),
    c_score NUMERIC(5,2) NOT NULL CHECK (c_score BETWEEN 0 AND 100),
    primary_type CHAR(1) NOT NULL CHECK (primary_type IN ('D','I','S','C')),
    secondary_type CHAR(1) CHECK (secondary_type IN ('D','I','S','C')),
    profile_code VARCHAR(10),
    interpretation TEXT,
    scoring_version VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE candidate_interests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    interest_code VARCHAR(100) NOT NULL,
    interest_name VARCHAR(255) NOT NULL,
    level interest_level NOT NULL,
    source VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(candidate_id, interest_code)
);

CREATE TABLE candidate_academic_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    version INT NOT NULL CHECK (version > 0),
    math_score NUMERIC(4,2) CHECK (math_score BETWEEN 0 AND 10),
    literature_score NUMERIC(4,2) CHECK (literature_score BETWEEN 0 AND 10),
    foreign_language_score NUMERIC(4,2) CHECK (foreign_language_score BETWEEN 0 AND 10),
    natural_science_score NUMERIC(4,2) CHECK (natural_science_score BETWEEN 0 AND 10),
    social_science_score NUMERIC(4,2) CHECK (social_science_score BETWEEN 0 AND 10),
    technology_score NUMERIC(4,2) CHECK (technology_score BETWEEN 0 AND 10),
    average_score NUMERIC(4,2) CHECK (average_score BETWEEN 0 AND 10),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(candidate_id, version)
);

CREATE TABLE major_disc_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    major_id UUID NOT NULL REFERENCES majors(id) ON DELETE CASCADE,
    d_weight NUMERIC(5,4) NOT NULL CHECK (d_weight BETWEEN 0 AND 1),
    i_weight NUMERIC(5,4) NOT NULL CHECK (i_weight BETWEEN 0 AND 1),
    s_weight NUMERIC(5,4) NOT NULL CHECK (s_weight BETWEEN 0 AND 1),
    c_weight NUMERIC(5,4) NOT NULL CHECK (c_weight BETWEEN 0 AND 1),
    version INT NOT NULL CHECK (version > 0),
    effective_from DATE NOT NULL,
    effective_to DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CHECK (ABS((d_weight + i_weight + s_weight + c_weight) - 1.0) < 0.0001),
    CHECK (effective_to IS NULL OR effective_to >= effective_from),
    UNIQUE(major_id, version)
);

CREATE TABLE major_interest_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    major_id UUID NOT NULL REFERENCES majors(id) ON DELETE CASCADE,
    interest_code VARCHAR(100) NOT NULL,
    weight NUMERIC(5,4) NOT NULL CHECK (weight BETWEEN 0 AND 1),
    UNIQUE(major_id, interest_code)
);

CREATE TABLE major_academic_requirements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    major_id UUID NOT NULL REFERENCES majors(id) ON DELETE CASCADE,
    subject_id UUID NOT NULL REFERENCES subjects(id),
    weight NUMERIC(5,4) NOT NULL CHECK (weight BETWEEN 0 AND 1),
    minimum_recommended_score NUMERIC(4,2)
        CHECK (minimum_recommended_score BETWEEN 0 AND 10),
    UNIQUE(major_id, subject_id)
);

CREATE TABLE recommendation_runs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    disc_attempt_id UUID REFERENCES test_attempts(id),
    academic_profile_version INT,
    interest_profile_version INT,
    algorithm_type recommendation_algorithm_type NOT NULL,
    model_name VARCHAR(255),
    model_version VARCHAR(100),
    generated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE major_recommendations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recommendation_run_id UUID NOT NULL REFERENCES recommendation_runs(id) ON DELETE CASCADE,
    major_id UUID NOT NULL REFERENCES majors(id),
    rank_position INT NOT NULL CHECK (rank_position > 0),
    disc_score NUMERIC(5,2) CHECK (disc_score BETWEEN 0 AND 100),
    academic_score NUMERIC(5,2) CHECK (academic_score BETWEEN 0 AND 100),
    interest_score NUMERIC(5,2) CHECK (interest_score BETWEEN 0 AND 100),
    career_goal_score NUMERIC(5,2) CHECK (career_goal_score BETWEEN 0 AND 100),
    admission_compatibility_score NUMERIC(5,2)
        CHECK (admission_compatibility_score BETWEEN 0 AND 100),
    total_score NUMERIC(5,2) NOT NULL CHECK (total_score BETWEEN 0 AND 100),
    explanation TEXT,
    UNIQUE(recommendation_run_id, rank_position),
    UNIQUE(recommendation_run_id, major_id)
);

CREATE TABLE recommendation_evidence (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recommendation_id UUID NOT NULL REFERENCES major_recommendations(id) ON DELETE CASCADE,
    evidence_type VARCHAR(100) NOT NULL,
    source_field VARCHAR(100),
    source_value TEXT,
    contribution_score NUMERIC(8,2),
    explanation TEXT
);

-- ============================================================
-- D. ONLINE ADMISSION APPLICATIONS
-- ============================================================

CREATE TABLE applications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_code VARCHAR(50) NOT NULL UNIQUE,
    candidate_id UUID NOT NULL REFERENCES users(id),
    admission_year_id UUID NOT NULL REFERENCES admission_years(id),
    admission_round_id UUID REFERENCES admission_rounds(id),
    admission_method_id UUID NOT NULL REFERENCES admission_methods(id),
    submitted_at TIMESTAMPTZ,
    current_status application_status NOT NULL DEFAULT 'DRAFT',
    reviewed_by UUID REFERENCES users(id),
    reviewed_at TIMESTAMPTZ,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE application_majors (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_id UUID NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    major_id UUID NOT NULL REFERENCES majors(id),
    program_id UUID REFERENCES programs(id),
    preference_order INT NOT NULL CHECK (preference_order > 0),
    subject_combo_id UUID REFERENCES subject_combos(id),
    calculated_score NUMERIC(5,2),
    eligibility_status verification_status NOT NULL DEFAULT 'PENDING',
    result_status application_status NOT NULL DEFAULT 'DRAFT',
    UNIQUE(application_id, preference_order),
    UNIQUE(application_id, major_id)
);

CREATE TABLE transcripts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    school_name VARCHAR(255) NOT NULL,
    graduation_year INT CHECK (graduation_year BETWEEN 1990 AND 2100),
    verification_status verification_status NOT NULL DEFAULT 'PENDING',
    verified_by UUID REFERENCES users(id),
    verified_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE transcript_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transcript_id UUID NOT NULL REFERENCES transcripts(id) ON DELETE CASCADE,
    academic_year VARCHAR(20) NOT NULL,
    semester INT CHECK (semester IN (1,2)),
    grade_level INT NOT NULL CHECK (grade_level BETWEEN 1 AND 12),
    subject_id UUID NOT NULL REFERENCES subjects(id),
    score NUMERIC(4,2) NOT NULL CHECK (score BETWEEN 0 AND 10),
    UNIQUE(transcript_id, academic_year, semester, grade_level, subject_id)
);

CREATE TABLE exam_scores (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    exam_type VARCHAR(100) NOT NULL,
    exam_year INT NOT NULL CHECK (exam_year BETWEEN 2000 AND 2100),
    subject_id UUID NOT NULL REFERENCES subjects(id),
    score NUMERIC(5,2) NOT NULL CHECK (score >= 0),
    candidate_number VARCHAR(100),
    verification_status verification_status NOT NULL DEFAULT 'PENDING',
    UNIQUE(candidate_id, exam_type, exam_year, subject_id)
);

CREATE TABLE application_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_id UUID NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    document_type document_type NOT NULL,
    file_url TEXT NOT NULL,
    original_filename VARCHAR(500) NOT NULL,
    mime_type VARCHAR(255),
    file_size BIGINT CHECK (file_size >= 0),
    verification_status verification_status NOT NULL DEFAULT 'PENDING',
    rejection_reason TEXT,
    uploaded_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    verified_by UUID REFERENCES users(id),
    verified_at TIMESTAMPTZ
);

CREATE TABLE application_status_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_id UUID NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    from_status application_status,
    to_status application_status NOT NULL,
    changed_by UUID NOT NULL REFERENCES users(id),
    reason TEXT,
    internal_note TEXT,
    visible_to_candidate BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- E. CHATBOT, HUMAN COUNSELING, QUEUE, APPOINTMENTS
-- ============================================================

CREATE TABLE chat_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id UUID REFERENCES users(id) ON DELETE SET NULL,
    university_id UUID REFERENCES universities(id) ON DELETE CASCADE,
    channel VARCHAR(50) NOT NULL DEFAULT 'WEB',
    session_type chat_session_type NOT NULL DEFAULT 'AI_ONLY',
    status chat_session_status NOT NULL DEFAULT 'OPEN',
    assigned_counselor_id UUID REFERENCES counselor_profiles(id),
    started_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    ended_at TIMESTAMPTZ
);

CREATE TABLE chat_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL REFERENCES chat_sessions(id) ON DELETE CASCADE,
    sender_type chat_sender_type NOT NULL,
    sender_user_id UUID REFERENCES users(id),
    message_type VARCHAR(50) NOT NULL DEFAULT 'TEXT',
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    read_at TIMESTAMPTZ
);

CREATE TABLE ai_message_metadata (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    message_id UUID NOT NULL UNIQUE REFERENCES chat_messages(id) ON DELETE CASCADE,
    model_name VARCHAR(255),
    model_version VARCHAR(100),
    confidence_score NUMERIC(5,4) CHECK (confidence_score BETWEEN 0 AND 1),
    prompt_version VARCHAR(100),
    retrieval_sources_json JSONB NOT NULL DEFAULT '[]'::jsonb,
    fallback_used BOOLEAN NOT NULL DEFAULT FALSE,
    processing_time_ms INT CHECK (processing_time_ms >= 0)
);

CREATE TABLE chat_handoffs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    chat_session_id UUID NOT NULL REFERENCES chat_sessions(id) ON DELETE CASCADE,
    requested_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    reason handoff_reason NOT NULL,
    priority INT NOT NULL DEFAULT 50 CHECK (priority BETWEEN 0 AND 100),
    assigned_counselor_id UUID REFERENCES counselor_profiles(id),
    accepted_at TIMESTAMPTZ,
    resolved_at TIMESTAMPTZ,
    status queue_status NOT NULL DEFAULT 'WAITING'
);

CREATE TABLE counselor_queue (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    chat_session_id UUID NOT NULL UNIQUE REFERENCES chat_sessions(id) ON DELETE CASCADE,
    queue_number BIGSERIAL UNIQUE,
    priority INT NOT NULL DEFAULT 50 CHECK (priority BETWEEN 0 AND 100),
    queued_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    assigned_at TIMESTAMPTZ,
    status queue_status NOT NULL DEFAULT 'WAITING'
);

CREATE TABLE appointments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id UUID NOT NULL REFERENCES users(id),
    counselor_id UUID NOT NULL REFERENCES counselor_profiles(id),
    appointment_type appointment_type NOT NULL,
    start_at TIMESTAMPTZ NOT NULL,
    end_at TIMESTAMPTZ NOT NULL,
    location TEXT,
    meeting_url TEXT,
    status appointment_status NOT NULL DEFAULT 'REQUESTED',
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CHECK (end_at > start_at)
);

-- ============================================================
-- F. CONTENT, EVENTS, ANALYTICS
-- ============================================================

CREATE TABLE posts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    university_id UUID NOT NULL REFERENCES universities(id) ON DELETE CASCADE,
    title VARCHAR(500) NOT NULL,
    slug VARCHAR(500) NOT NULL,
    summary TEXT,
    content TEXT NOT NULL,
    post_type VARCHAR(100),
    status post_status NOT NULL DEFAULT 'DRAFT',
    author_id UUID REFERENCES users(id),
    published_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(university_id, slug)
);

CREATE TABLE events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    university_id UUID NOT NULL REFERENCES universities(id) ON DELETE CASCADE,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    event_type VARCHAR(100),
    start_at TIMESTAMPTZ NOT NULL,
    end_at TIMESTAMPTZ,
    location TEXT,
    registration_url TEXT,
    status generic_status NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CHECK (end_at IS NULL OR end_at >= start_at)
);

CREATE TABLE analytics_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    anonymous_id VARCHAR(255),
    session_id VARCHAR(255),
    event_type analytics_event_type NOT NULL,
    entity_type VARCHAR(100),
    entity_id UUID,
    source VARCHAR(255),
    campaign VARCHAR(255),
    metadata_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- G. NOTIFICATIONS, AUDIT, CONSENT, SETTINGS, TOKENS
-- ============================================================

CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    notification_type VARCHAR(100) NOT NULL,
    title VARCHAR(500) NOT NULL,
    content TEXT NOT NULL,
    channel notification_channel NOT NULL,
    status notification_status NOT NULL DEFAULT 'PENDING',
    sent_at TIMESTAMPTZ,
    read_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(150) NOT NULL,
    entity_type VARCHAR(150) NOT NULL,
    entity_id UUID,
    before_data_json JSONB,
    after_data_json JSONB,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE user_consents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    consent_type VARCHAR(100) NOT NULL,
    policy_version VARCHAR(50) NOT NULL,
    accepted BOOLEAN NOT NULL,
    accepted_at TIMESTAMPTZ,
    revoked_at TIMESTAMPTZ,
    UNIQUE(user_id, consent_type, policy_version)
);

CREATE TABLE system_settings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    university_id UUID REFERENCES universities(id) ON DELETE CASCADE,
    setting_key VARCHAR(150) NOT NULL,
    setting_value TEXT,
    data_type VARCHAR(50) NOT NULL DEFAULT 'STRING',
    description TEXT,
    updated_by UUID REFERENCES users(id),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX uq_system_setting
ON system_settings (
    COALESCE(university_id, '00000000-0000-0000-0000-000000000000'::uuid),
    setting_key
);

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_ip VARCHAR(64),
    user_agent TEXT
);

CREATE TABLE password_reset_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    used_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_ip VARCHAR(64)
);

-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX idx_users_university ON users(university_id);
CREATE INDEX idx_users_status ON users(account_status);
CREATE INDEX idx_user_roles_user ON user_roles(user_id);
CREATE INDEX idx_user_roles_role ON user_roles(role_id);

CREATE INDEX idx_majors_university ON majors(university_id);
CREATE INDEX idx_majors_faculty ON majors(faculty_id);
CREATE INDEX idx_majors_status ON majors(status);

CREATE INDEX idx_admission_years_university ON admission_years(university_id);
CREATE INDEX idx_admission_rounds_year ON admission_rounds(admission_year_id);
CREATE INDEX idx_major_plans_year ON major_admission_plans(admission_year_id);
CREATE INDEX idx_major_plans_major ON major_admission_plans(major_id);

CREATE INDEX idx_test_attempts_user ON test_attempts(user_id);
CREATE INDEX idx_test_attempts_test ON test_attempts(test_id);
CREATE INDEX idx_disc_scores_attempt ON disc_scores(attempt_id);

CREATE INDEX idx_recommendation_runs_candidate ON recommendation_runs(candidate_id);
CREATE INDEX idx_major_recommendations_run ON major_recommendations(recommendation_run_id);
CREATE INDEX idx_major_recommendations_score ON major_recommendations(total_score DESC);

CREATE INDEX idx_applications_candidate ON applications(candidate_id);
CREATE INDEX idx_applications_status ON applications(current_status);
CREATE INDEX idx_applications_year ON applications(admission_year_id);
CREATE INDEX idx_application_history_application ON application_status_history(application_id);

CREATE INDEX idx_chat_sessions_candidate ON chat_sessions(candidate_id);
CREATE INDEX idx_chat_sessions_status ON chat_sessions(status);
CREATE INDEX idx_chat_messages_session_created ON chat_messages(session_id, created_at);
CREATE INDEX idx_queue_status_priority ON counselor_queue(status, priority DESC, queued_at ASC);

CREATE INDEX idx_posts_status_published ON posts(status, published_at DESC);
CREATE INDEX idx_analytics_event_created ON analytics_events(event_type, created_at DESC);
CREATE INDEX idx_notifications_user_status ON notifications(user_id, status);
CREATE INDEX idx_audit_actor_created ON audit_logs(actor_user_id, created_at DESC);
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expiry ON refresh_tokens(expires_at);
CREATE INDEX idx_password_reset_tokens_user ON password_reset_tokens(user_id);
CREATE INDEX idx_password_reset_tokens_expiry ON password_reset_tokens(expires_at);
CREATE INDEX idx_password_reset_tokens_hash ON password_reset_tokens(token_hash);

CREATE INDEX idx_conditions_json_gin ON major_admission_methods USING GIN (conditions_json);
CREATE INDEX idx_analytics_metadata_gin ON analytics_events USING GIN (metadata_json);
CREATE INDEX idx_ai_sources_gin ON ai_message_metadata USING GIN (retrieval_sources_json);

-- ============================================================
-- UPDATED_AT TRIGGERS
-- ============================================================

CREATE TRIGGER trg_universities_updated_at
BEFORE UPDATE ON universities
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_users_updated_at
BEFORE UPDATE ON users
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_roles_updated_at
BEFORE UPDATE ON roles
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_candidate_profiles_updated_at
BEFORE UPDATE ON candidate_profiles
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_parent_profiles_updated_at
BEFORE UPDATE ON parent_profiles
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_counselor_profiles_updated_at
BEFORE UPDATE ON counselor_profiles
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_faculties_updated_at
BEFORE UPDATE ON faculties
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_majors_updated_at
BEFORE UPDATE ON majors
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_programs_updated_at
BEFORE UPDATE ON programs
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_admission_years_updated_at
BEFORE UPDATE ON admission_years
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_admission_rounds_updated_at
BEFORE UPDATE ON admission_rounds
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_admission_methods_updated_at
BEFORE UPDATE ON admission_methods
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_major_admission_plans_updated_at
BEFORE UPDATE ON major_admission_plans
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_major_admission_methods_updated_at
BEFORE UPDATE ON major_admission_methods
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_tests_updated_at
BEFORE UPDATE ON tests
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_test_questions_updated_at
BEFORE UPDATE ON test_questions
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_applications_updated_at
BEFORE UPDATE ON applications
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_posts_updated_at
BEFORE UPDATE ON posts
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_events_updated_at
BEFORE UPDATE ON events
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ============================================================
-- BUSINESS FUNCTIONS
-- ============================================================

CREATE OR REPLACE FUNCTION can_transition_application_status(
    p_from application_status,
    p_to application_status
)
RETURNS BOOLEAN AS $$
BEGIN
    RETURN CASE
        WHEN p_from = 'DRAFT' AND p_to = 'SUBMITTED' THEN TRUE
        WHEN p_from = 'SUBMITTED' AND p_to IN ('UNDER_REVIEW', 'WITHDRAWN') THEN TRUE
        WHEN p_from = 'UNDER_REVIEW' AND p_to IN (
            'NEED_ADDITIONAL_DOCUMENTS',
            'ELIGIBLE',
            'INELIGIBLE',
            'WITHDRAWN'
        ) THEN TRUE
        WHEN p_from = 'NEED_ADDITIONAL_DOCUMENTS' AND p_to IN (
            'SUBMITTED',
            'UNDER_REVIEW',
            'WITHDRAWN'
        ) THEN TRUE
        WHEN p_from = 'ELIGIBLE' AND p_to IN (
            'ADMITTED',
            'NOT_ADMITTED',
            'WITHDRAWN'
        ) THEN TRUE
        WHEN p_from = 'ADMITTED' AND p_to IN (
            'CONFIRMED',
            'WITHDRAWN'
        ) THEN TRUE
        WHEN p_from = 'CONFIRMED' AND p_to IN (
            'ENROLLED',
            'WITHDRAWN'
        ) THEN TRUE
        ELSE FALSE
    END;
END;
$$ LANGUAGE plpgsql IMMUTABLE;

CREATE OR REPLACE FUNCTION validate_application_status_transition()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.current_status IS DISTINCT FROM NEW.current_status THEN
        IF NOT can_transition_application_status(OLD.current_status, NEW.current_status) THEN
            RAISE EXCEPTION
                'Invalid application status transition: % -> %',
                OLD.current_status,
                NEW.current_status;
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validate_application_status
BEFORE UPDATE OF current_status ON applications
FOR EACH ROW EXECUTE FUNCTION validate_application_status_transition();

-- Prevent editing published tests
CREATE OR REPLACE FUNCTION prevent_published_test_structure_update()
RETURNS TRIGGER AS $$
DECLARE
    v_status test_status;
BEGIN
    SELECT status INTO v_status
    FROM tests
    WHERE id = COALESCE(NEW.test_id, OLD.test_id);

    IF v_status = 'PUBLISHED' THEN
        RAISE EXCEPTION 'Published test structure cannot be modified. Create a new version.';
    END IF;

    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_prevent_published_question_update
BEFORE INSERT OR UPDATE OR DELETE ON test_questions
FOR EACH ROW EXECUTE FUNCTION prevent_published_test_structure_update();

-- ============================================================
-- SEED DATA: HUBT, ROLES, PERMISSIONS
-- ============================================================

INSERT INTO universities (
    code,
    name,
    short_name,
    description,
    status
)
VALUES (
    'HUBT',
    'Trường Đại học Kinh doanh và Công nghệ Hà Nội',
    'HUBT',
    'Dữ liệu khởi tạo cho hệ thống HUBT Assistant',
    'ACTIVE'
)
ON CONFLICT (code) DO NOTHING;

INSERT INTO roles (code, name, description, priority, scope, system_role)
VALUES
('SYSTEM_ADMIN', 'Quản trị hệ thống', 'Quản trị kỹ thuật toàn nền tảng', 100, 'SYSTEM', TRUE),
('UNIVERSITY_ADMIN', 'Quản trị nhà trường', 'Quản lý nghiệp vụ toàn trường', 80, 'UNIVERSITY', TRUE),
('ADMISSION_MANAGER', 'Quản lý tuyển sinh', 'Quản lý kế hoạch và hoạt động tuyển sinh', 70, 'UNIVERSITY', TRUE),
('ADMISSION_OFFICER', 'Cán bộ tuyển sinh', 'Kiểm tra và xử lý hồ sơ', 60, 'UNIVERSITY', TRUE),
('COUNSELOR', 'Tư vấn viên', 'Tư vấn thí sinh và tiếp nhận chat', 50, 'UNIVERSITY', TRUE),
('CONTENT_EDITOR', 'Biên tập nội dung', 'Quản lý bài viết và sự kiện', 40, 'UNIVERSITY', TRUE),
('LECTURER', 'Giảng viên', 'Cung cấp thông tin chuyên môn ngành', 30, 'UNIVERSITY', TRUE),
('CANDIDATE', 'Thí sinh', 'Người dùng đăng ký tư vấn và xét tuyển', 20, 'SELF', TRUE),
('PARENT', 'Phụ huynh', 'Đồng hành cùng thí sinh', 10, 'SELF', TRUE),
('GUEST', 'Khách', 'Người dùng chưa đăng nhập', 0, 'SELF', TRUE)
ON CONFLICT (code) DO NOTHING;

INSERT INTO permissions (code, name, module, description)
VALUES
('USER_VIEW', 'Xem người dùng', 'IDENTITY', NULL),
('USER_CREATE', 'Tạo người dùng', 'IDENTITY', NULL),
('USER_UPDATE', 'Cập nhật người dùng', 'IDENTITY', NULL),
('USER_LOCK', 'Khóa người dùng', 'IDENTITY', NULL),
('ROLE_VIEW', 'Xem vai trò', 'IDENTITY', NULL),
('ROLE_ASSIGN', 'Gán vai trò', 'IDENTITY', NULL),
('ROLE_REVOKE', 'Thu hồi vai trò', 'IDENTITY', NULL),
('PERMISSION_MANAGE', 'Quản lý quyền', 'IDENTITY', NULL),

('MAJOR_VIEW', 'Xem ngành', 'UNIVERSITY', NULL),
('MAJOR_CREATE', 'Tạo ngành', 'UNIVERSITY', NULL),
('MAJOR_UPDATE', 'Cập nhật ngành', 'UNIVERSITY', NULL),
('MAJOR_DELETE', 'Xóa ngành', 'UNIVERSITY', NULL),

('ADMISSION_YEAR_MANAGE', 'Quản lý năm tuyển sinh', 'ADMISSION', NULL),
('ADMISSION_ROUND_MANAGE', 'Quản lý đợt tuyển sinh', 'ADMISSION', NULL),
('ADMISSION_METHOD_MANAGE', 'Quản lý phương thức', 'ADMISSION', NULL),
('QUOTA_MANAGE', 'Quản lý chỉ tiêu', 'ADMISSION', NULL),
('CUTOFF_MANAGE', 'Quản lý điểm chuẩn', 'ADMISSION', NULL),

('TEST_VIEW', 'Xem bài đánh giá', 'ASSESSMENT', NULL),
('TEST_CREATE', 'Tạo bài đánh giá', 'ASSESSMENT', NULL),
('TEST_UPDATE', 'Sửa bài đánh giá', 'ASSESSMENT', NULL),
('TEST_PUBLISH', 'Xuất bản bài đánh giá', 'ASSESSMENT', NULL),
('TEST_RESULT_VIEW', 'Xem kết quả bài đánh giá', 'ASSESSMENT', NULL),

('APPLICATION_CREATE', 'Tạo hồ sơ', 'APPLICATION', NULL),
('APPLICATION_VIEW_SELF', 'Xem hồ sơ cá nhân', 'APPLICATION', NULL),
('APPLICATION_VIEW_ALL', 'Xem tất cả hồ sơ', 'APPLICATION', NULL),
('APPLICATION_REVIEW', 'Kiểm tra hồ sơ', 'APPLICATION', NULL),
('APPLICATION_REQUEST_UPDATE', 'Yêu cầu bổ sung hồ sơ', 'APPLICATION', NULL),
('APPLICATION_APPROVE', 'Duyệt hồ sơ', 'APPLICATION', NULL),
('APPLICATION_REJECT', 'Từ chối hồ sơ', 'APPLICATION', NULL),
('APPLICATION_EXPORT', 'Xuất hồ sơ', 'APPLICATION', NULL),

('CHAT_START', 'Bắt đầu chat', 'CHAT', NULL),
('CHAT_VIEW_SELF', 'Xem chat cá nhân', 'CHAT', NULL),
('CHAT_VIEW_ASSIGNED', 'Xem chat được giao', 'CHAT', NULL),
('CHAT_ASSIGN', 'Phân công chat', 'CHAT', NULL),
('CHAT_REPLY', 'Trả lời chat', 'CHAT', NULL),
('CHAT_CLOSE', 'Đóng chat', 'CHAT', NULL),

('REPORT_VIEW', 'Xem báo cáo', 'REPORT', NULL),
('REPORT_EXPORT', 'Xuất báo cáo', 'REPORT', NULL),
('ANALYTICS_VIEW', 'Xem thống kê', 'ANALYTICS', NULL)
ON CONFLICT (code) DO NOTHING;

-- SYSTEM_ADMIN gets all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.code = 'SYSTEM_ADMIN'
ON CONFLICT DO NOTHING;

-- UNIVERSITY_ADMIN gets all except permission management
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.code = 'UNIVERSITY_ADMIN'
  AND p.code <> 'PERMISSION_MANAGE'
ON CONFLICT DO NOTHING;

-- Candidate permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
    'MAJOR_VIEW',
    'TEST_VIEW',
    'TEST_RESULT_VIEW',
    'APPLICATION_CREATE',
    'APPLICATION_VIEW_SELF',
    'CHAT_START',
    'CHAT_VIEW_SELF'
)
WHERE r.code = 'CANDIDATE'
ON CONFLICT DO NOTHING;

-- Counselor permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
    'USER_VIEW',
    'MAJOR_VIEW',
    'TEST_RESULT_VIEW',
    'CHAT_VIEW_ASSIGNED',
    'CHAT_REPLY',
    'CHAT_CLOSE'
)
WHERE r.code = 'COUNSELOR'
ON CONFLICT DO NOTHING;

-- Admission officer permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
    'USER_VIEW',
    'MAJOR_VIEW',
    'APPLICATION_VIEW_ALL',
    'APPLICATION_REVIEW',
    'APPLICATION_REQUEST_UPDATE',
    'APPLICATION_APPROVE',
    'APPLICATION_REJECT',
    'APPLICATION_EXPORT'
)
WHERE r.code = 'ADMISSION_OFFICER'
ON CONFLICT DO NOTHING;

-- ============================================================
-- VIEWS
-- ============================================================

CREATE OR REPLACE VIEW v_user_permissions AS
SELECT DISTINCT
    u.id AS user_id,
    u.email,
    r.code AS role_code,
    r.priority,
    p.code AS permission_code,
    ur.university_id
FROM users u
JOIN user_roles ur
    ON ur.user_id = u.id
   AND ur.active = TRUE
   AND (ur.expired_at IS NULL OR ur.expired_at > NOW())
JOIN roles r
    ON r.id = ur.role_id
   AND r.active = TRUE
JOIN role_permissions rp
    ON rp.role_id = r.id
JOIN permissions p
    ON p.id = rp.permission_id
WHERE u.deleted_at IS NULL
  AND u.account_status = 'ACTIVE';

CREATE OR REPLACE VIEW v_latest_disc_score AS
SELECT DISTINCT ON (ta.user_id)
    ta.user_id,
    ds.*
FROM test_attempts ta
JOIN disc_scores ds ON ds.attempt_id = ta.id
WHERE ta.status = 'COMPLETED'
ORDER BY ta.user_id, ta.submitted_at DESC NULLS LAST;

CREATE OR REPLACE VIEW v_latest_recommendations AS
SELECT DISTINCT ON (rr.candidate_id, mr.major_id)
    rr.candidate_id,
    rr.id AS recommendation_run_id,
    rr.generated_at,
    mr.major_id,
    m.code AS major_code,
    m.name AS major_name,
    mr.rank_position,
    mr.total_score,
    mr.explanation
FROM recommendation_runs rr
JOIN major_recommendations mr ON mr.recommendation_run_id = rr.id
JOIN majors m ON m.id = mr.major_id
ORDER BY rr.candidate_id, mr.major_id, rr.generated_at DESC;

-- ============================================================
-- COMMENTS
-- ============================================================

COMMENT ON TABLE disc_scores IS
'Kết quả đánh giá xu hướng hành vi DISC, không phải chẩn đoán tâm lý lâm sàng.';

COMMENT ON TABLE major_recommendations IS
'Kết quả gợi ý ngành; không được sử dụng như quyết định trúng tuyển chính thức.';

COMMENT ON TABLE audit_logs IS
'Nhật ký thao tác quản trị và nghiệp vụ nhạy cảm; không nên cho phép sửa hoặc xóa từ ứng dụng.';

-- ============================================================
-- END
-- ============================================================



"EDITTTTTTTTTTTTTT"
CREATE TABLE hubt.password_reset_otps (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES hubt.users(id) ON DELETE CASCADE,
    otp_hash VARCHAR(64) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    used_at TIMESTAMPTZ,
    failed_attempts INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_ip VARCHAR(64)
);
DROP TABLE IF EXISTS hubt.password_reset_tokens CASCADE;
CREATE TABLE hubt.password_reset_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id UUID NOT NULL
        REFERENCES hubt.users(id)
        ON DELETE CASCADE,

    otp_hash VARCHAR(64) NOT NULL,

    expires_at TIMESTAMPTZ NOT NULL,

    verified_at TIMESTAMPTZ,

    reset_token_hash VARCHAR(64) UNIQUE,

    reset_token_expires_at TIMESTAMPTZ,

    failed_attempts INT NOT NULL DEFAULT 0
        CHECK (failed_attempts >= 0),

    used_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    created_ip VARCHAR(64)
);

CREATE INDEX idx_password_reset_tokens_user
ON hubt.password_reset_tokens(user_id);

CREATE INDEX idx_password_reset_tokens_otp_expiry
ON hubt.password_reset_tokens(expires_at);

CREATE INDEX idx_password_reset_tokens_reset_hash
ON hubt.password_reset_tokens(reset_token_hash);
CREATE SEQUENCE IF NOT EXISTS hubt.candidate_code_seq
START WITH 1
INCREMENT BY 1
MINVALUE 1;

SELECT
    column_name,
    data_type,
    udt_name,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_schema = 'hubt'
  AND table_name = 'candidate_academic_profiles'
ORDER BY ordinal_position;