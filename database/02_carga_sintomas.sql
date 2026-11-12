INSERT INTO sintoma (nome, descricao) VALUES
    ('Febre', 'Elevação da temperatura corporal.'),
    ('Lesões', 'Lesões na pele ou no corpo.'),
    ('Dor no corpo', 'Dor muscular ou generalizada.'),
    ('Inchaço', 'Aumento de volume em alguma região do corpo.'),
    ('Dificuldade respiratória', 'Dificuldade ou desconforto para respirar.'),
    ('Náuseas', 'Enjoo ou vontade de vomitar.'),
    ('Fadiga', 'Cansaço ou fraqueza fora do comum.'),
    ('Tontura', 'Sensação de desequilíbrio ou cabeça leve.'),
    ('Mau-estar', 'Sensação geral de indisposição.'),
    ('Dor de cabeça', 'Dor na região da cabeça.'),
    ('Arrepios', 'Sensação de frio com tremores.'),
    ('Aumento da frequência cardíaca', 'Batimentos cardíacos acelerados.'),
    ('Suor frio', 'Suor acompanhado de sensação de frio.'),
    ('Sudorese', 'Suor em excesso.'),
    ('Perda de apetite', 'Redução da vontade de comer.'),
    ('Vermelhidão no rosto', 'Rosto avermelhado.')
ON CONFLICT (nome) DO NOTHING;
