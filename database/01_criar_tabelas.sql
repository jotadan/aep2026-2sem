CREATE TABLE IF NOT EXISTS paciente (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    data_nascimento DATE NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    contato_emergencia VARCHAR(150) NOT NULL
);

CREATE TABLE IF NOT EXISTS sintoma (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL UNIQUE,
    descricao TEXT
);

CREATE TABLE IF NOT EXISTS triagem (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL,
    paciente_id BIGINT NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    prioridade VARCHAR(20),
    observacao TEXT,
    CONSTRAINT fk_triagem_paciente FOREIGN KEY (paciente_id) REFERENCES paciente (id),
    CONSTRAINT ck_triagem_tipo CHECK (tipo IN ('ADULTO', 'PEDIATRICA')),
    CONSTRAINT ck_triagem_prioridade CHECK (prioridade IN ('VERDE', 'AMARELO', 'VERMELHO'))
);

CREATE TABLE IF NOT EXISTS sintoma_relatado (
    id BIGSERIAL PRIMARY KEY,
    triagem_id BIGINT NOT NULL,
    sintoma_id BIGINT NOT NULL,
    intensidade INTEGER NOT NULL,
    duracao_horas INTEGER NOT NULL,
    observacao TEXT,
    CONSTRAINT fk_relatado_triagem FOREIGN KEY (triagem_id) REFERENCES triagem (id) ON DELETE CASCADE,
    CONSTRAINT fk_relatado_sintoma FOREIGN KEY (sintoma_id) REFERENCES sintoma (id),
    CONSTRAINT ck_relatado_intensidade CHECK (intensidade BETWEEN 1 AND 10),
    CONSTRAINT ck_relatado_duracao CHECK (duracao_horas >= 0)
);

CREATE TABLE IF NOT EXISTS resultado_triagem (
    id BIGSERIAL PRIMARY KEY,
    triagem_id BIGINT NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    sinais_associados TEXT,
    orientacao TEXT,
    nivel_atendimento VARCHAR(30) NOT NULL,
    CONSTRAINT fk_resultado_triagem FOREIGN KEY (triagem_id) REFERENCES triagem (id) ON DELETE CASCADE,
    CONSTRAINT ck_resultado_nivel CHECK (nivel_atendimento IN ('ORIENTACAO_DOMICILIAR', 'UBS', 'UPA', 'EMERGENCIA'))
);

CREATE TABLE IF NOT EXISTS encaminhamento (
    id BIGSERIAL PRIMARY KEY,
    triagem_id BIGINT NOT NULL UNIQUE,
    nivel_atendimento VARCHAR(30) NOT NULL,
    orientacao TEXT,
    CONSTRAINT fk_encaminhamento_triagem FOREIGN KEY (triagem_id) REFERENCES triagem (id) ON DELETE CASCADE,
    CONSTRAINT ck_encaminhamento_nivel CHECK (nivel_atendimento IN ('ORIENTACAO_DOMICILIAR', 'UBS', 'UPA', 'EMERGENCIA'))
);

CREATE INDEX IF NOT EXISTS idx_triagem_paciente ON triagem (paciente_id);
CREATE INDEX IF NOT EXISTS idx_relatado_triagem ON sintoma_relatado (triagem_id);
CREATE INDEX IF NOT EXISTS idx_resultado_triagem ON resultado_triagem (triagem_id);
