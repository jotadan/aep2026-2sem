SELECT id, nome, cpf, data_nascimento,
       EXTRACT(YEAR FROM AGE(data_nascimento)) AS idade,
       telefone, contato_emergencia
FROM paciente
ORDER BY nome;

SELECT t.id, p.nome AS paciente, t.tipo, t.data_hora, t.prioridade, e.nivel_atendimento
FROM triagem t
JOIN paciente p ON p.id = t.paciente_id
LEFT JOIN encaminhamento e ON e.triagem_id = t.id
ORDER BY t.data_hora DESC;

SELECT t.id AS triagem, s.nome AS sintoma, sr.intensidade, sr.duracao_horas, sr.observacao
FROM sintoma_relatado sr
JOIN triagem t ON t.id = sr.triagem_id
JOIN sintoma s ON s.id = sr.sintoma_id
ORDER BY t.id, sr.intensidade DESC;

SELECT prioridade, COUNT(*) AS total
FROM triagem
GROUP BY prioridade
ORDER BY CASE prioridade WHEN 'VERMELHO' THEN 1 WHEN 'AMARELO' THEN 2 ELSE 3 END;

SELECT e.nivel_atendimento, COUNT(*) AS total
FROM encaminhamento e
GROUP BY e.nivel_atendimento
ORDER BY total DESC;

SELECT s.nome, COUNT(*) AS ocorrencias, ROUND(AVG(sr.intensidade), 1) AS intensidade_media
FROM sintoma_relatado sr
JOIN sintoma s ON s.id = sr.sintoma_id
GROUP BY s.nome
ORDER BY ocorrencias DESC, s.nome;

SELECT p.nome, COUNT(t.id) AS triagens, MAX(t.data_hora) AS ultima_triagem
FROM paciente p
LEFT JOIN triagem t ON t.paciente_id = p.id
GROUP BY p.id, p.nome
ORDER BY triagens DESC, p.nome;
