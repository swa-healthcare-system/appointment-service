-- init-scripts/init.sql
CREATE TABLE IF NOT EXISTS appointments (
    "id" SERIAL PRIMARY KEY,
    "doctorId" VARCHAR(36) NOT NULL,
    "patientId" VARCHAR(36) NOT NULL,
    "fromTS" VARCHAR(50) NOT NULL,
    "toTS" VARCHAR(50) NOT NULL,
    "note" VARCHAR(255),
    "status" VARCHAR(50) NOT NULL
    );
