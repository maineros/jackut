# Jackut - Rede Social de Relacionamentos
O Jackut é um sistema de rede social inspirado no Orkut, permitindo que usuários criem contas, montem perfis, adicionem amigos e troquem recados entre si. Este projeto foi desenvolvido como parte da disciplina de *Programação 2*, com foco total em **Programação Orientada a Objetos (POO)** e a **linguagem de programação Java**.

## Funcionalidades (User Stories)
O sistema foi construído de forma incremental, atendendo aos seguintes requisitos:

- **US1 - Gestão de Contas**: Criação de conta com login, senha e nome, e autenticação via sessão com ID único. ✅
- **US2 - Edição de Perfil**: Criação e edição livre de atributos de perfil (descrição, cidade natal, estado civil, aniversário, entre outros). ✅
- **US3 - Sistema de Amizades**: Adição de amigos com convite e aceite bilateral; a amizade só é confirmada quando ambos os lados se adicionam. ✅
- **US4 - Recados**: Envio e leitura de recados entre usuários em fila FIFO por destinatário. ✅

Cada US possui dois scripts de teste: o `_1` executa os cenários principais e o `_2` valida a persistência dos dados após encerramento do sistema.

## Arquitetura e Padrões de Projeto
O projeto segue uma arquitetura em camadas com foco em encapsulamento e responsabilidade única:

- **Padrão Facade (Fachada)**: A classe `Facade` centraliza todas as chamadas do EasyAccept, atuando como um despachante puro sem nenhuma lógica de negócio.
- **Rich Domain Model**: A entidade `Usuario` é responsável pela própria integridade; valida seus dados na construção, encapsula suas coleções e lança exceções diretamente ao detectar estados inválidos.
- **Controlador**: Orquestra o fluxo de negócio, gerencia sessões ativas e cuida da persistência em disco.
- **Exceções Atômicas**: Cada violação de regra de negócio possui sua própria classe de exceção no pacote `exceptions`, todas herdando de `JackutException`, garantindo mensagens exatas e rastreabilidade.

## Estrutura do Projeto
```plaintext
src/
├── entities/
│   └── Usuario.java               # modelo rico: perfil, amizades e recados
├── exceptions/
│   ├── JackutException.java       # exceção base do domínio
│   ├── AtributoNaoPreenchidoException.java
│   ├── AutoAmizadeException.java
│   ├── AutoRecadoException.java
│   ├── ContaJaExisteException.java
│   ├── ConvitePendenteException.java
│   ├── LoginInvalidoException.java
│   ├── LoginOuSenhaInvalidosException.java
│   ├── SemRecadosException.java
│   ├── SenhaInvalidaException.java
│   ├── UsuarioJaAmigoException.java
│   └── UsuarioNaoCadastradoException.java
├── Controlador.java               # orquestrador: sessões, fluxo e persistência
├── Facade.java                    # porta de entrada do EasyAccept
└── Main.java                      # executor dos testes em cascata
testes/                            # scripts de teste (.txt) fornecidos pelo docente
lib/
└── easyaccept.jar                 # biblioteca de testes de aceitação
data/
└── dados.dat                      # persistência (criada automaticamente)
```

## Como executar
### Pré-requisitos
- JDK 11 ou superior instalado
- Arquivo `easyaccept.jar` em `lib/`

### Compilação
```bash
# Windows
javac -encoding UTF-8 -cp "lib\easyaccept.jar" -d bin src\exceptions\*.java src\entities\*.java src\*.java
```

### Execução dos testes
Para rodar a bateria completa de testes através da classe `Main`:
```bash
# Windows
java -cp "bin;lib\easyaccept.jar" Main
```
```bash
# Linux/macOS
java -cp "bin:lib/easyaccept.jar" Main
```

Para rodar um teste individualmente:
```bash
java "-Dfile.encoding=ISO-8859-1" -cp "bin;lib\easyaccept.jar" easyaccept.EasyAccept Facade testes\us1_1.txt
```

## Qualidade e Testes
O projeto foi validado utilizando a ferramenta EasyAccept, garantindo que 100% das regras de negócio descritas nos scripts de teste fossem atendidas; incluindo tratamento correto de exceções, persistência entre execuções e formatos de saída exatos.

## Autora e Contato
| Nome | Contato |
| ---- | ------- |
| Laura Mainero | lblrm@ic.ufal.br |
