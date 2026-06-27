# Jackut - Rede Social de Relacionamentos
O Jackut é um sistema de rede social inspirado no Orkut, permitindo que usuários criem contas, montem perfis, adicionem amigos, participem de comunidades, troquem recados e estabeleçam novos tipos de relacionamento. Este projeto foi desenvolvido como parte da disciplina de *Programação 2*, com foco total em **Programação Orientada a Objetos (POO)** e a **linguagem de programação Java**.

## Funcionalidades (User Stories)
O sistema foi construído de forma incremental, atendendo aos seguintes requisitos:

- **US1 - Gestão de Contas**: Criação de conta com login, senha e nome, e autenticação via sessão com ID único. ✅
- **US2 - Edição de Perfil**: Criação e edição livre de atributos de perfil (descrição, cidade natal, estado civil, aniversário, entre outros). ✅
- **US3 - Sistema de Amizades**: Adição de amigos com convite e aceite bilateral; a amizade só é confirmada quando ambos os lados se adicionam. ✅
- **US4 - Recados**: Envio e leitura de recados privados entre usuários em fila FIFO. ✅
- **US5 - Criação de Comunidades**: Criação de comunidades com nome único, descrição e dono. ✅
- **US6 - Participação em Comunidades**: Adesão de usuários a comunidades existentes. ✅
- **US7 - Mensagens em Comunidades**: Envio de mensagens a comunidades; todos os membros as recebem em fila FIFO. ✅
- **US8 - Novos Relacionamentos**: Relacionamentos de fã/ídolo, paquera (com notificação automática de match mútuo) e inimizade (com bloqueio de interações). ✅
- **US9 - Remoção de Conta**: Encerramento de conta com remoção em cascata de comunidades, relacionamentos e recados pendentes. ✅

Cada US possui dois scripts de teste: o `_1` executa os cenários principais e o `_2` valida a persistência dos dados após encerramento do sistema.

## Arquitetura e Padrões de Projeto
O projeto segue uma arquitetura em camadas com foco em encapsulamento e responsabilidade única:

- **Padrão Facade**: A classe `Facade` é a única porta de entrada do EasyAccept, atuando como despachante puro sem nenhuma lógica de negócio.
- **Rich Domain Model**: As entidades `Usuario` e `Comunidade` são responsáveis pela própria integridade — validam seus dados, encapsulam coleções e lançam exceções diretamente ao detectar estados inválidos.
- **Tell, Don't Ask**: Operações de relacionamento (`tentarAdicionarAmigo`, `tentarAdicionarIdolo`, `tentarAdicionarPaquera`, `tentarAdicionarInimigo`) são encapsuladas nas entidades, que validam, decidem e lançam a exceção adequada sem precisar ser interrogadas pelo Controlador antes.
- **Controlador**: Orquestra o fluxo entre entidades. Não contém lógica de sessão nem de I/O — delega essas responsabilidades às classes especializadas.
- **GerenciadorSessoes**: Responsável exclusivamente pelo ciclo de vida das sessões ativas.
- **Repositorio**: Responsável exclusivamente pela persistência em disco (serialização/desserialização).
- **Exceções Atômicas (checked)**: Cada violação de regra de negócio tem sua própria classe de exceção, todas herdando de `JackutException extends Exception`. Isso obriga cada método a declarar explicitamente via `throws` os erros que pode lançar. Falhas de infraestrutura usam `PersistenciaException extends RuntimeException`, uma hierarquia separada.

## Estrutura do Projeto
```plaintext
src/jackut/
├── Facade.java                        # porta de entrada do EasyAccept
├── Main.java                          # executor dos testes em cascata
├── controllers/
│   └── Controlador.java               # orquestra fluxo de negócio entre entidades
├── services/
│   └── GerenciadorSessoes.java        # ciclo de vida das sessões ativas
├── repositories/
│   ├── Repositorio.java               # serialização em data/
│   └── PersistenciaException.java     # erro de I/O (unchecked)
├── entities/
│   ├── Usuario.java                   # modelo rico: perfil, amizades, recados e relacionamentos US8
│   └── Comunidade.java                # modelo rico: membros e dissolução em cascata
└── exceptions/
    ├── JackutException.java           # base checked (extends Exception)
    ├── LoginInvalidoException.java
    ├── SenhaInvalidaException.java
    ├── ContaJaExisteException.java
    ├── LoginOuSenhaInvalidosException.java
    ├── UsuarioNaoCadastradoException.java
    ├── AtributoNaoPreenchidoException.java
    ├── AutoAmizadeException.java
    ├── UsuarioJaAmigoException.java
    ├── ConvitePendenteException.java
    ├── AutoRecadoException.java
    ├── SemRecadosException.java
    ├── ComunidadeJaExisteException.java
    ├── ComunidadeNaoExisteException.java
    ├── UsuarioJaMembroException.java
    ├── SemMensagensException.java
    ├── AutoIdoloException.java
    ├── UsuarioJaIdoloException.java
    ├── AutoPaqueraException.java
    ├── UsuarioJaPaqueraException.java
    ├── AutoInimigoException.java
    ├── UsuarioJaInimigoException.java
    └── InimigoException.java
testes/          # scripts de teste fornecidos pelo docente (us1_1.txt … us9_2.txt)
lib/
└── easyaccept.jar
data/            # criada automaticamente na primeira execução
├── dados.dat
└── comunidades.dat
```

## Como executar
### Pré-requisitos
- JDK 11 ou superior
- Arquivo `easyaccept.jar` em `lib/`

### Compilação
```powershell
# Windows
javac -encoding UTF-8 -cp "lib\easyaccept.jar" -d bin src\jackut\exceptions\*.java src\jackut\entities\*.java src\jackut\repositories\*.java src\jackut\services\*.java src\jackut\controllers\*.java src\jackut\Facade.java src\jackut\Main.java
```
```bash
# Linux/macOS
javac -encoding UTF-8 -cp "lib/easyaccept.jar" -d bin src/jackut/exceptions/*.java src/jackut/entities/*.java src/jackut/repositories/*.java src/jackut/services/*.java src/jackut/controllers/*.java src/jackut/Facade.java src/jackut/Main.java
```

### Rodar todos os testes
```powershell
# Windows
java -cp "bin;lib\easyaccept.jar" jackut.Main
```
```bash
# Linux/macOS
java -cp "bin:lib/easyaccept.jar" jackut.Main
```

### Rodar um teste individualmente
```powershell
java "-Dfile.encoding=ISO-8859-1" -cp "bin;lib\easyaccept.jar" easyaccept.EasyAccept jackut.Facade testes\us1_1.txt
```

## Qualidade e Testes
O projeto foi validado com 100% de aprovação nos testes de aceitação do EasyAccept, totalizando 476 asserções distribuídas em 18 scripts (9 User Stories × 2 arquivos cada).

## Autora e Contato
| Nome | Contato |
| ---- | ------- |
| Laura Mainero | lblrm@ic.ufal.br |
