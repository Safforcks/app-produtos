# app-produtos

**Produtos, categorias e persistência em arquivos com Java.**

O `app-produtos` é uma aplicação de console para cadastrar e consultar produtos e categorias. O projeto explora orientação a objetos, interfaces genéricas, enumerações, associação entre objetos e leitura e escrita de arquivos.

A persistência é feita em **`categoria.txt` e `produtos.txt`**. Os dados continuam disponíveis entre execuções quando o programa utiliza o mesmo diretório de trabalho. Não é necessário configurar um servidor de banco de dados.

[Operações disponíveis](#usando-o-menu) · [Modelo e arquitetura](#como-o-código-está-organizado) · [Persistência](#onde-os-dados-ficam) · [Execução](#executando-o-projeto) · [Referências](#fontes-e-referências)

## Visão do projeto

<p align="center">
  <a href="https://github.com/user-attachments/assets/3cc72900-148d-44c3-acb3-427da93659df">
    <img src="https://github.com/user-attachments/assets/3cc72900-148d-44c3-acb3-427da93659df" alt="Diagrama do projeto app-produtos" width="100%" />
  </a>
</p>

*Clique na imagem para abrir a versão original.*

## Usando o menu

Ao iniciar `software.Main`, o programa apresenta quatro opções:

```text
OPÇÕES DE MENU:

    (1) CADASTRAR CATEGORIA
    (2) CONSULTAR CATEGORIAS
    (3) CADASTRAR PRODUTOS
    (4) CONSULTAR PRODUTOS

INFORME A OPÇÃO DESEJADA:
```

| Opção | O que você informa | O que acontece |
| --- | --- | --- |
| `1` — Cadastrar categoria | Nome e descrição. | Gera um UUID e acrescenta a categoria a `categoria.txt`. |
| `2` — Consultar categorias | Basta escolher a opção; não há outros dados a informar. | Lê `categoria.txt` e exibe ID, nome e descrição das categorias. |
| `3` — Cadastrar produto | Nome, preço, quantidade, tipo, nome e descrição da categoria. | Gera os IDs do produto e de sua categoria e acrescenta os dados a `produtos.txt`. |
| `4` — Consultar produtos | Basta escolher a opção; não há outros dados a informar. | Lê `produtos.txt` e exibe ID, nome, preço, quantidade, tipo, nome e descrição da categoria. |

O programa executa uma operação e encerra. Para realizar outra, inicie novamente a classe principal.

As consultas seguem a ordem dos registros no arquivo. Não possuem filtros, ordenação adicional ou paginação. Atualização e exclusão ainda não fazem parte do projeto; portanto, o escopo atual cobre cadastro e consulta, sem um CRUD completo.

### Tipos de produto

| Entrada no cadastro | Valor da enumeração gravado no arquivo |
| --- | --- |
| `1` | `PERECIVEL` |
| `2` | `NAO_PERECIVEL` |
| `3` | `DIGITAL` |

O tipo classifica o produto. Ele não aciona regras específicas de validade, entrega ou estoque: não há campo de vencimento para perecíveis nem tratamento adicional para produtos digitais.

## Como o código está organizado

### Responsabilidade de cada parte

| Arquivo | Papel na aplicação |
| --- | --- |
| [Main.java](src/main/java/software/Main.java) | Exibe o menu, cria os objetos necessários, chama os serviços e imprime as listas retornadas. |
| [entities/Produto.java](src/main/java/software/entities/Produto.java) | Representa um produto e sua associação com uma categoria. |
| [entities/Categoria.java](src/main/java/software/entities/Categoria.java) | Representa o ID, o nome e a descrição de uma categoria. |
| [enums/TipoProduto.java](src/main/java/software/enums/TipoProduto.java) | Define as três classificações permitidas no modelo. |
| [interfaces/BaseService.java](src/main/java/software/interfaces/BaseService.java) | Define o contrato genérico de cadastro e consulta. |
| [interfaces/ProdutoService.java](src/main/java/software/interfaces/ProdutoService.java) | Especializa o contrato para `Produto`. |
| [interfaces/CategoriaService.java](src/main/java/software/interfaces/CategoriaService.java) | Especializa o contrato para `Categoria`. |
| [services/ProdutoServiceImpl.java](src/main/java/software/services/ProdutoServiceImpl.java) | Recebe dados do produto pelo console, grava o arquivo e reconstrói os objetos na consulta. |
| [services/CategoriaServiceImpl.java](src/main/java/software/services/CategoriaServiceImpl.java) | Recebe dados da categoria pelo console, grava o arquivo e reconstrói os objetos na consulta. |
| [pom.xml](pom.xml) | Define o projeto Maven, a configuração Java e a dependência do Lombok. |

Nesta versão, os serviços concentram entrada pelo terminal, persistência e mensagens de resultado. Não há uma camada separada de repositórios.

### Interfaces e generics

O contrato compartilhado é:

```java
public interface BaseService<T> {
    void cadastrar(T entidade);
    List<T> consultar();
}
```

O parâmetro `T` permite usar a mesma definição com entidades diferentes:

- `CategoriaService extends BaseService<Categoria>` trabalha com categorias.
- `ProdutoService extends BaseService<Produto>` trabalha com produtos.
- `CategoriaServiceImpl` e `ProdutoServiceImpl` implementam os respectivos contratos.

Assim, o contrato do serviço de produtos exige `cadastrar(Produto)` e `List<Produto> consultar()`. O contrato de categorias exige os equivalentes para `Categoria`.

O método `cadastrar` recebe uma entidade, mas sua implementação atual solicita e preenche os dados pelo console. Ele não se limita a salvar um objeto já preenchido.

### Encapsulamento e Lombok

As entidades possuem atributos privados e usam `@Data` do Lombok para gerar métodos como getters, setters, `toString`, `equals` e `hashCode`.

Essa geração reduz código repetitivo, mas não acrescenta validação de nome, preço ou quantidade. As regras precisam ser implementadas explicitamente.

### Modelo de domínio

| Entidade | Atributo | Tipo Java |
| --- | --- | --- |
| `Categoria` | `id` | `UUID` |
| `Categoria` | `nome` | `String` |
| `Categoria` | `descricao` | `String` |
| `Produto` | `id` | `UUID` |
| `Produto` | `nome` | `String` |
| `Produto` | `preco` | `Double` |
| `Produto` | `quantidade` | `Integer` |
| `Produto` | `tipo` | `TipoProduto` |
| `Produto` | `categoria` | `Categoria` |

O atributo `Produto.categoria` representa uma associação entre objetos. `Produto` não herda de `Categoria`.

Antes de cadastrar um produto, `Main` executa `produto.setCategoria(new Categoria())`. Essa inicialização é necessária para o serviço acessar `produto.getCategoria().setNome(...)` e os demais métodos da categoria.

## Uma diferença importante entre os cadastros

**Cadastrar uma categoria pela opção 1 não a disponibiliza para seleção na opção 3.**

No fluxo atual, os dois cadastros são independentes:

- A opção `1` salva uma categoria em `categoria.txt`.
- A opção `3` solicita novamente o nome e a descrição da categoria, gera outro UUID e grava esses dados dentro de `produtos.txt`.
- O cadastro de produto não consulta `categoria.txt`, não pede um ID de categoria existente e não grava a nova categoria nesse arquivo.

Por isso, duas categorias com o mesmo nome podem ter IDs diferentes. Os dados da categoria associada ao produto ficam incorporados ao registro do próprio produto, sem sincronização com as categorias cadastradas separadamente.

## Onde os dados ficam

### Persistência atual

O projeto **não utiliza banco SQL, JDBC, tabelas ou chaves estrangeiras**. Também não possui script de criação de banco nem configuração Docker. O armazenamento é realizado com arquivos locais:

| Arquivo | Conteúdo | Quantidade de linhas de dados por registro |
| --- | --- | --- |
| [categoria.txt](categoria.txt) | UUID, nome e descrição da categoria. | 3 |
| [produtos.txt](produtos.txt) | Dados do produto e da categoria associada. | 8 |

Os nomes são caminhos relativos. Os arquivos são procurados e gravados no **diretório de trabalho do processo Java**, que pode ser diferente da pasta do código se a IDE ou o terminal forem configurados de outra forma.

O repositório já contém arquivos de exemplo. Novos cadastros são acrescentados ao conteúdo existente por `new FileWriter(arquivo, true)`, sem apagar os registros anteriores.

### Formato de categoria.txt

A ordem de um registro é:

```text
<UUID da categoria>
<nome>
<descrição>
...
```

O cadastro escreve uma quebra de linha depois de cada campo e termina com `...`, sem acrescentar outra quebra de linha após esse marcador.

Assim, ao cadastrar outra categoria, o próximo UUID pode ficar junto ao separador:

```text
<UUID da primeira categoria>
<nome>
<descrição>
...<UUID da segunda categoria>
<nome>
<descrição>
...
```

A consulta trata esse formato: remove o prefixo `...` da linha candidata a ID e ignora a linha quando ela fica vazia. Em seguida, converte o UUID e lê os dois campos seguintes.

### Formato de produtos.txt

Exemplo de registro, correspondente aos dados de produto presentes no repositório:

```text
3fb1182d-447a-4d92-a107-4abcd579dc44
Notebook Dell
6500.0
10
NAO_PERECIVEL
353d0d32-26de-4abe-a199-ef7148403226
Informática
Informática & Tecnologia
...
```

| Posição | Dado gravado | Conversão realizada na consulta |
| --- | --- | --- |
| 1 | ID do produto. | `UUID.fromString`. |
| 2 | Nome do produto. | Texto. |
| 3 | Preço. | `Double.parseDouble`. |
| 4 | Quantidade. | `Integer.parseInt`. |
| 5 | Tipo. | `TipoProduto.valueOf`. |
| 6 | ID da categoria associada. | `UUID.fromString`. |
| 7 | Nome da categoria associada. | Texto. |
| 8 | Descrição da categoria associada. | Texto. |

O serviço de produtos usa o mesmo tratamento do prefixo `...` adotado na consulta de categorias. Não se trata de CSV: os campos são identificados pela posição de cada linha.

Alterar a ordem, remover linhas ou inserir valores incompatíveis pode impedir a leitura. O nome do enum precisa ser exatamente `PERECIVEL`, `NAO_PERECIVEL` ou `DIGITAL`.

### Recursos utilizados na leitura e na escrita

Os serviços usam `Scanner` para ler os arquivos e `PrintWriter` com `FileWriter` para escrever. Esses recursos de arquivo são abertos em blocos `try-with-resources`.

As consultas montam listas em memória e retornam `List<Categoria>` ou `List<Produto>`. Se ocorrer erro durante a leitura, o serviço imprime a mensagem e retorna a lista construída até aquele momento, que pode estar vazia ou incompleta.

Não há controle transacional, integridade referencial ou tratamento próprio para gravações simultâneas.

## Regras que existem hoje

| Assunto | Comportamento implementado |
| --- | --- |
| Identificação | Os cadastros geram UUIDs com `UUID.randomUUID()`. O usuário não digita o ID. |
| Nome e descrição | São lidos como texto, sem validação de obrigatoriedade, tamanho ou espaços em branco. |
| Preço | É convertido com `Double.parseDouble`. Para um decimal usual, use ponto, como `89.90`, sem símbolo de moeda. |
| Quantidade | É convertida com `Integer.parseInt`; precisa ter formato inteiro. |
| Valores negativos | Não há validação para impedir preço ou quantidade negativos ou zero. |
| Tipo | As opções `1`, `2` e `3` são convertidas para os três valores do enum. |
| Tipo fora das opções | O `switch` não possui `default`. No fluxo do menu, o atributo permanece nulo e pode ser escrito como `null`, causando falha em `TipoProduto.valueOf` numa consulta posterior. |
| Categoria do produto | Deve estar instanciada antes de chamar `cadastrar`; o fluxo de `Main` faz isso. |
| Duplicidade | Nomes e outros dados podem se repetir. Não há busca ou restrição de unicidade. |
| Consulta | Retorna registros na ordem do arquivo, sem filtro ou classificação adicional. |
| Lista vazia | Não há mensagem específica de ausência de registros; a lista não produz itens no laço de exibição. |
| Datas | As entidades e o formato dos arquivos não registram data de cadastro ou validade. |
| Repetição do menu | Não existe laço para executar várias operações na mesma inicialização. |

### Como os erros aparecem

As conversões numéricas do menu e do cadastro acontecem fora dos blocos que tratam erros de arquivo. Uma opção não numérica, um preço com vírgula ou uma quantidade fracionária podem encerrar a execução com exceção.

As operações de arquivo apresentam as mensagens definidas em cada serviço:

| Operação | Prefixo da mensagem |
| --- | --- |
| Gravar categoria | `FALHA AO GRAVAR O ARQUIVO:` |
| Consultar categorias | `FALHA AO LER ARQUIVO:` |
| Gravar produto | `ERRO AO GRAVAR ARQUIVO:` |
| Consultar produtos | `ERRO AO LER ARQUIVO:` |

Um arquivo inexistente não é tratado como uma consulta vazia: a tentativa de leitura gera uma mensagem de erro. No cadastro, o arquivo pode ser criado automaticamente se o diretório existir e houver permissão de escrita.

## Executando o projeto

### Ambiente necessário

O [pom.xml](pom.xml) declara:

| Item | Configuração |
| --- | --- |
| Java | Source e target `21`. |
| Projeto Maven | `software:app-produtos:1.0-SNAPSHOT`. |
| Lombok | `1.18.48`, com escopo `provided`. |
| Codificação do projeto | `UTF-8`. |

É necessário ter JDK 21, Maven e acesso às dependências para a primeira compilação. Não há Maven Wrapper no repositório.

Confira:

```bash
java -version
mvn -version
```

O Maven também deve estar utilizando o JDK 21.

### Obter o código

```bash
git clone https://github.com/Safforcks/app-produtos.git
cd app-produtos
```

Utilize a raiz do projeto como diretório de trabalho para acessar os arquivos de exemplo já existentes.

### Pela IDE

Importe o projeto como Maven, configure o JDK 21 e o suporte ao processamento de anotações do Lombok. Execute a classe `software.Main` em um console interativo.

Confira o campo de diretório de trabalho da configuração de execução. Ele determina onde os arquivos serão lidos e gravados.

### Pelo terminal

Na raiz do projeto:

```bash
mvn clean compile
mvn org.codehaus.mojo:exec-maven-plugin:3.5.0:java -Dexec.mainClass=software.Main
```

O segundo comando invoca explicitamente o Exec Maven Plugin para executar a classe principal com as dependências do projeto, sem exigir alteração no `pom.xml`.

A execução usa entrada pelo terminal. Não há JAR executável com classe principal configurada no `pom.xml`; os comandos acima ou a IDE são os caminhos de execução indicados aqui.

## Roteiro para experimentar

### Cadastrar e consultar uma categoria

1. Inicie o programa e escolha `1`.
2. Informe o nome `Acessórios`.
3. Informe a descrição `Periféricos e acessórios para computadores`.
4. Após a mensagem de gravação, inicie novamente o programa e escolha `2`.
5. Confira a categoria na lista e o novo conteúdo de `categoria.txt`.

### Cadastrar e consultar um produto

Inicie o programa e escolha `3`. Use, por exemplo:

| Campo | Valor de exemplo |
| --- | --- |
| Nome do produto | `Teclado USB` |
| Preço | `89.90` |
| Quantidade | `10` |
| Tipo | `2` — não perecível |
| Nome da categoria | `Acessórios` |
| Descrição da categoria | `Periféricos e acessórios para computadores` |

Inicie novamente e escolha `4`. A consulta deve apresentar os dados gravados, com um UUID gerado para o produto. O preço é exibido pela conversão padrão de `Double`, sem formatação monetária brasileira.

Mesmo utilizando o nome `Acessórios` do primeiro exemplo, o cadastro do produto cria outro UUID de categoria. Não há seleção ou reaproveitamento automático da categoria salva separadamente.

Os exemplos acrescentam registros aos arquivos. Executá-los novamente cria novos cadastros.

## Quando algo não funciona

| Sintoma | O que verificar |
| --- | --- |
| `invalid target release: 21` ou erro semelhante | Configure o JDK 21 na IDE e no Maven. |
| Getters e setters não encontrados | Confira o carregamento do Lombok e o processamento de anotações. |
| Maven não resolve o Lombok | Confira o acesso ao repositório de dependências e a disponibilidade da versão declarada no `pom.xml`. |
| `NumberFormatException` no menu | Digite uma opção numérica de `1` a `4`. |
| Erro ao informar preço | Use `89.90`, sem vírgula, separador de milhar ou `R$`. |
| Erro ao informar quantidade | Use um inteiro, como `10`. |
| `No enum constant` na consulta | Confira a linha do tipo no arquivo. `null` ou um nome diferente dos valores do enum não pode ser convertido. |
| Falha ao converter UUID | Confira a linha do ID e a integridade da sequência de campos. |
| Erro de arquivo inexistente | Verifique se o arquivo está no diretório de trabalho da execução. |
| Cadastros aparecem em outra pasta | Ajuste o diretório de trabalho; os caminhos dos arquivos são relativos. |
| Apenas parte dos registros aparece | Um registro inválido pode interromper a leitura. Confira a mensagem e o conteúdo após o último registro lido. |
| `NullPointerException` ao cadastrar por outro código | Inicialize a categoria do produto antes de chamar o serviço, como `Main` já faz. |
| Categoria cadastrada não é oferecida ao criar produto | É o comportamento atual: os fluxos usam registros independentes. |
| Falha de escrita | Confira se a pasta de execução existe e permite criar ou alterar os arquivos. |

## Próximos passos possíveis

As melhorias abaixo ainda não estão implementadas:

- Validar campos em branco, limites e formatos antes de persistir.
- Tratar opções inválidas de tipo e impedir a gravação de `null`.
- Definir regras de negócio para preço, quantidade e duplicidade.
- Permitir selecionar uma categoria cadastrada e decidir como manter seu vínculo com o produto.
- Separar a interação de console da persistência, criando repositórios de arquivos.
- Padronizar o formato dos registros e a codificação de leitura e escrita.
- Tratar arquivos ausentes e registros inválidos com mensagens mais específicas.
- Acrescentar repetição do menu, busca, atualização e exclusão.
- Utilizar `BigDecimal` quando o preço exigir tratamento decimal próprio para valores monetários.
- Adicionar testes automatizados para persistência, conversão e regras.
- Se o escopo evoluir para banco relacional, definir tabelas de produtos e categorias, chave estrangeira e migrações.

O repositório ainda não contém testes automatizados. O roteiro de cadastro e consulta fornece uma verificação manual do fluxo atual.

## Fontes e referências

As funcionalidades, regras e formatos descritos têm como fonte o próprio código:

- [Main — menu e exibição dos resultados](src/main/java/software/Main.java)
- [Produto — atributos e associação com Categoria](src/main/java/software/entities/Produto.java)
- [Categoria — atributos](src/main/java/software/entities/Categoria.java)
- [TipoProduto — valores permitidos no modelo](src/main/java/software/enums/TipoProduto.java)
- [BaseService — contrato genérico](src/main/java/software/interfaces/BaseService.java)
- [CategoriaService](src/main/java/software/interfaces/CategoriaService.java) e [ProdutoService](src/main/java/software/interfaces/ProdutoService.java)
- [CategoriaServiceImpl — gravação e consulta](src/main/java/software/services/CategoriaServiceImpl.java)
- [ProdutoServiceImpl — gravação e consulta](src/main/java/software/services/ProdutoServiceImpl.java)
- [categoria.txt — exemplos persistidos](categoria.txt) e [produtos.txt — exemplo persistido](produtos.txt)
- [pom.xml — configuração e dependência](pom.xml)

Documentação oficial para aprofundamento:

- [Java 21 — FileWriter](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/FileWriter.html)
- [Java 21 — Scanner](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Scanner.html)
- [Java 21 — UUID](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/UUID.html)
- [Java — Interfaces genéricas](https://docs.oracle.com/javase/tutorial/java/generics/types.html)
- [Lombok — @Data](https://projectlombok.org/features/Data)
- [Apache Maven — Introdução](https://maven.apache.org/guides/getting-started/)
- [MojoHaus — Exec Maven Plugin](https://www.mojohaus.org/exec-maven-plugin/)
