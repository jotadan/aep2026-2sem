# Triagem Comunitária de Sintomas

Aplicação de console em Java para cadastro de pacientes, registro de triagens de sintomas, cálculo de
prioridade e geração de encaminhamento. O projeto faz parte da AEP do 2º semestre de 2026 (Engenharia de
Software) e se alinha à ODS 3 (Saúde e Bem-Estar).

O sistema **não fornece diagnóstico médico**. Sua função é orientar sobre o nível de atendimento a procurar.
Os critérios de classificação são didáticos, definidos pelo projeto, e não foram validados clinicamente.

## Sumário

1. [Tecnologias](#tecnologias)
2. [Requisitos](#requisitos)
3. [Configuração do banco de dados](#configuração-do-banco-de-dados)
4. [Como executar](#como-executar)
5. [Menu do sistema](#menu-do-sistema)
6. [Testes](#testes)
7. [Requisitos funcionais](#requisitos-funcionais)
8. [Fluxo de registro de triagem](#fluxo-de-registro-de-triagem)
9. [Regras de classificação](#regras-de-classificação)
10. [Organização do código](#organização-do-código)
11. [Modelo de domínio e banco de dados](#modelo-de-domínio-e-banco-de-dados)
12. [Origem dos sintomas](#origem-dos-sintomas)
13. [Limitações](#limitações)
14. [Próximas etapas](#próximas-etapas)

## Tecnologias

- Java 21
- Maven
- JPA com Hibernate (sem Spring Boot)
- PostgreSQL, usado ao executar o sistema
- H2 em memória, usado apenas nos testes automáticos (escopo `test`, não vai para o jar)

## Requisitos

- Java 21 ou superior
- Maven 3.9 ou superior
- PostgreSQL em execução (ou Docker, para subir um container)

## Configuração do banco de dados

Crie um banco chamado `triagem`. As tabelas são criadas automaticamente na primeira execução.

A porta padrão do projeto é a **5433**, para não conflitar com um PostgreSQL instalado na máquina, que
costuma usar a 5432. A conexão é configurada por variáveis de ambiente. Se não forem definidas, valem os
valores padrão:

| Variável | Padrão |
|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5433/triagem` |
| `DB_USER` | `postgres` |
| `DB_PASSWORD` | `postgres` |

Exemplo com Docker:

```bash
docker run -d --name triagem-pg -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=triagem -p 5433:5432 postgres
```

Depois de criado, o container pode ser religado com `docker start triagem-pg`.

Para usar outro servidor, porta ou senha, defina as variáveis antes de executar. No PowerShell:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/triagem"
$env:DB_USER = "postgres"
$env:DB_PASSWORD = "sua_senha"
```

Na primeira execução o catálogo de sintomas é gravado na tabela `sintoma`.

### Erro de conexão

Se o sistema mostrar "Não foi possível conectar ao banco de dados", a linha seguinte ("Motivo") indica a
causa. Os casos mais comuns são servidor desligado, porta errada, senha incorreta ou banco `triagem`
inexistente.

## Como executar

```bash
mvn clean package
java -jar target/untitled-1.0-SNAPSHOT.jar
```

O pacote gerado inclui todas as dependências. O `mvn clean package` também roda os testes e falha se algum
deles falhar. Feche o sistema antes de empacotar de novo, pois o Windows não deixa apagar um jar em uso.

## Menu do sistema

```
1. Cadastrar paciente
2. Consultar paciente
3. Atualizar paciente
4. Excluir paciente
5. Listar pacientes
6. Registrar triagem
7. Histórico de triagens do paciente
8. Consultar triagem
9. Atualizar triagem
10. Excluir triagem
0. Sair
```

## Testes

```bash
mvn test
```

Os testes ficam em `src/test/java/br/com/triagemcomunitaria/TriagemTest.java`, são escritos em Java puro
e executados pelo Maven na fase `test`. Eles usam um banco H2 em memória, portanto não exigem PostgreSQL e
não alteram os dados do banco real. A entrada do usuário no console é simulada. Cobertura:

- validações do modelo e das regras de prioridade de adultos e crianças;
- níveis de encaminhamento e geração de resultados;
- CRUD de pacientes e de triagens;
- tratamento de entradas inválidas e cancelamento de operações;
- as nove categorias de sintomas e o fluxo de febre;
- proteção contra exclusão de pacientes com triagens.

## Requisitos funcionais

| Requisito | Situação |
|---|---|
| RF01 Cadastro de pacientes | Cadastro, consulta, atualização, exclusão e listagem |
| RF02 Registro de triagem com sintomas, intensidade e duração | Vários sintomas por triagem |
| RF03 Prioridade automática, com regras para adultos e crianças | Tipo definido pela idade do cadastro |
| RF04 Encaminhamento com orientação em linguagem simples | Gerado ao concluir a triagem |
| RF05 Consultar, atualizar, excluir triagens e listar histórico | Menu principal, opções 7 a 10 |

Validações presentes: campos obrigatórios, CPF com 11 dígitos e único, data de nascimento existente e não
futura, tamanhos máximos conforme o DER, intensidade de 1 a 10, duração não negativa, opções de menu
inexistentes, entradas não numéricas e exclusão de paciente com triagens. Digitar `0` ao pedir um ID cancela
a operação.

## Fluxo de registro de triagem

1. Informar o ID do paciente.
2. O tipo da triagem é definido pela idade: menores de 18 anos fazem a triagem pediátrica.
3. Selecionar uma categoria de sintoma e informar intensidade, duração e observação.
4. Repetir para outros sintomas. A opção `0` conclui o registro. Sem sintomas, a triagem é cancelada.
5. Informar observações gerais.
6. O sistema calcula a prioridade, gera um resultado por sintoma e o encaminhamento, e grava tudo no banco.

Na categoria **Febre**, o sistema pergunta a temperatura:

- abaixo de 37 °C: a febre não é registrada;
- a partir de 37 °C: a febre é registrada e o sistema oferece a lista de sintomas associados;
- a partir de 39 °C: o sistema também exibe um alerta para procurar atendimento médico.

A temperatura informada é gravada na observação do sintoma relatado.

## Regras de classificação

O RF03 exige regras distintas para adultos e crianças, mas o documento da AEP não define os critérios.
Foram adotadas as regras abaixo, simples e baseadas em intensidade (I) e duração em horas (D) de cada
sintoma. A prioridade da triagem é a maior entre as prioridades de seus sintomas. Elas ficam nos métodos
`classificar` de `TriagemAdulto` e `TriagemPediatrica`, e podem ser ajustadas em um único lugar.

| Prioridade | Adulto | Pediátrica |
|---|---|---|
| Vermelho | I ≥ 9, ou dificuldade respiratória com I ≥ 7 | I ≥ 8, ou dificuldade respiratória com I ≥ 5 |
| Amarelo | I ≥ 6, ou I ≥ 4 com D > 72 | I ≥ 5, ou I ≥ 3 com D > 48, ou febre com D > 24 |
| Verde | Demais casos | Demais casos |

Encaminhamento:

| Prioridade | Nível de atendimento |
|---|---|
| Verde | Orientação domiciliar |
| Amarelo | UBS |
| Vermelho | UPA, ou Emergência se houver intensidade 10 ou dificuldade respiratória grave |

As orientações em linguagem simples ficam no enum `NivelAtendimento`. Ao atualizar uma triagem, a prioridade,
os resultados e o encaminhamento são recalculados.

## Organização do código

O projeto é dividido em pacotes dentro de `br.com.triagemcomunitaria`:

```
Main.java        inicia o sistema (conexão, catálogo e menu)
view/            menus e telas do console
model/           classes do domínio (entidades JPA e enums)
dao/             acesso ao banco de dados
service/         regras de negócio e validações
util/            conexão com o banco, leitura do console e formatação
```

| Pacote | Classe | Responsabilidade |
|---|---|---|
| (raiz) | `Main` | Abre a conexão, carrega o catálogo e inicia o menu |
| `view` | `MenuPrincipal` | Menu principal e tratamento dos erros das operações |
| `view` | `PacienteView` | Telas de cadastro, consulta, atualização, exclusão e listagem de pacientes |
| `view` | `TriagemView` | Telas de registro, histórico, consulta, atualização e exclusão de triagens |
| `view` | `SintomaView` | Menus de sintomas, incluindo o submenu de febre |
| `model` | `Pessoa` | Superclasse com dados pessoais e `calcularIdade()` |
| `model` | `Paciente` | Especializa `Pessoa` com contato de emergência e `atualizarDados()` |
| `model` | `Sintoma` | Sintoma do catálogo e `atualizarDescricao()` |
| `model` | `SintomaRelatado` | Ocorrência de um `Sintoma` em uma triagem, com `validarIntensidade()` |
| `model` | `Triagem` | Classe abstrata com `calcularPrioridade()`, `gerarEncaminhamento()` e `concluir()` |
| `model` | `TriagemAdulto` | Regras de classificação para adultos |
| `model` | `TriagemPediatrica` | Regras de classificação para crianças |
| `model` | `ResultadoTriagem` | Resultado por sintoma, com `gerarResumo()` |
| `model` | `Encaminhamento` | Encaminhamento final, com `gerarOrientacao()` |
| `model` | `Prioridade`, `NivelAtendimento` | Enums do UML |
| `dao` | `PacienteDAO`, `SintomaDAO`, `TriagemDAO` | Gravam e buscam dados com JPA |
| `service` | `PacienteService` | CPF único, campos obrigatórios, data válida e bloqueio de exclusão com triagens |
| `service` | `TriagemService` | Define o tipo da triagem pela idade, conclui, grava e atualiza |
| `service` | `SintomaService` | Carrega o catálogo de sintomas |
| `util` | `Conexao` | Abre e fecha a conexão com o PostgreSQL |
| `util` | `Entrada` | Leitura e validação das entradas do console |
| `util` | `Formatador` | Máscara de CPF e formatação de datas |

O mapeamento JPA é feito por anotações nas classes do `model` e a unidade de persistência está em
`src/main/resources/META-INF/persistence.xml`.

## Modelo de domínio e banco de dados

O modelo segue `docs/DiagramaClasse.png` e `docs/DiagramaBanco.png`.

- `Pessoa` é uma superclasse mapeada (`@MappedSuperclass`), e `Paciente` corresponde à tabela `paciente`.
- `Triagem` usa herança de tabela única na tabela `triagem`. A coluna `tipo` guarda `ADULTO` ou `PEDIATRICA`.
- `SintomaRelatado` é a tabela associativa entre `triagem` e `sintoma`, com `intensidade`, `duracao_horas`
  e `observacao`.
- `ResultadoTriagem` corresponde a `resultado_triagem`, e `Encaminhamento` a `encaminhamento`, com
  `triagem_id` único (relação 1 para 1).
- Os sintomas relatados, resultados e o encaminhamento pertencem à triagem. Ao excluir uma triagem, eles
  são removidos junto.
- O catálogo de sintomas é gravado uma vez e referenciado, nunca copiado.

## Origem dos sintomas

O catálogo foi construído a partir do programa em C que antecedeu esta versão (`Aep.c`). Esse programa
estava incompleto e apresentava falhas, tratadas na migração:

- apenas o menu de febre possuía fluxo implementado, as demais categorias existiam só no menu principal;
- o menu principal listava **Fadiga** no lugar de "Pressão arterial", e a migração seguiu o programa;
- havia uma variável não declarada (`decisao2`), o que impedia a compilação;
- a variável `decisao1` mantinha valor antigo quando a temperatura era menor que 37 °C.

Catálogo atual:

- categorias do menu principal: Febre, Lesões, Dor no corpo, Inchaço, Dificuldade respiratória,
  Náuseas, Fadiga, Tontura e Mau-estar;
- sintomas do submenu de febre: Dor de cabeça, Arrepios, Aumento da frequência cardíaca, Suor frio,
  Sudorese, Perda de apetite e Vermelhidão no rosto. "Mal estar geral" e "Fadiga e cansaço" reutilizam
  as entradas de Mau-estar e Fadiga.

As listas de possíveis doenças e as recomendações de medicamentos do programa original não foram
incorporadas, pois o sistema não emite diagnóstico.

## Próximas etapas

- Validar as regras de classificação com profissionais de saúde.
- Criar submenus para as demais categorias de sintomas.
- Considerar a temperatura no cálculo da prioridade.
- Usar migrações de banco (por exemplo Flyway) no lugar da criação automática de tabelas.