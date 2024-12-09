# Sistema de Gerenciamento de Cliente

## Sobre o Projeto
Desenvolvemos um sistema de gestão de cadastro de clientes para uma empresa global, com foco em manipular grandes volumes de dados. O sistema deve ser eficiente no gerenciamento de recursos, utilizando algoritmos de ordenação externa para otimizar o uso de memória ao lidar com arquivos de grande porte, além de oferecer uma interface gráfica amigável e funcional.

Com nosso programa é possível:
1. Carregar um arquivo de clientes
2. Pesquisar clientes
3. Inserir novos clientes
4. Remover clientes
5. Ordenar os clientes

![img.png](img.png)
### Para acessar o código, rode o comando abaixo:
```bash
git clone https://github.com/Theo-Prog10/tpa_ordena-ao_externa
```
## Tecnologias Usadas
* Java
* Maven

## Principais Bibliotecas Usadas
* Java Faker
* javax.swing

## Diagrama de Classes

```mermaid
classDiagram
direction BT
class ArquivoCliente {
  + abrirArquivo(String, String, Class~Cliente~) void
  + fechaArquivo() void
  + escreveNoArquivo(List~Cliente~) void
  + leiaDoArquivo(int) List~Cliente~
}
class ArquivoSequencial~T~ {
<<Interface>>
  + leiaDoArquivo(int) List~T~
  + escreveNoArquivo(List~T~) void
  + fechaArquivo() void
  + abrirArquivo(String, String, Class~T~) void
}
class Buffer~T~ {
<<Interface>>
  + inicializaBuffer(String, String) void
  + carregaBuffer() void
  + associaBuffer(ArquivoSequencial~T~) void
  + adicionaAoBuffer(Cliente) void
  + escreveBuffer() void
  + fechaBuffer() void
}
class BufferDeClientes {
  + adicionaAoBuffer(Cliente) void
  + associaBuffer(ArquivoSequencial~Cliente~) void
  + inicializaBuffer(String, String) void
  + proximoCliente() Cliente
  + fechaBuffer() void
  + carregaBuffer() void
  + escreveBuffer() void
  + proximosClientes(int) Cliente[]
}
class Cliente {
  + toString() String
  + compareTo(Cliente) int
   String telefone
   String endereco
   String nome
   String sobrenome
   int creditScore
}
class ClienteGUI {
  - criarInterface() void
  + main(String[]) void
  - carregarClientes() void
}
class ClienteGUI2 {
  + main(String[]) void
  - pesquisarCliente() void
  - atualizarTabela() void
  - inserirCliente() void
  - ordenarClientes() void
  - carregarArquivo() void
  - carregarMaisClientes() void
  - removerCliente() void
  - criarInterface() void
}
class GeradorDeArquivosDeClientes {
  + geraGrandeDataSetDeClientes(String, int) void
  + testeGeracaoClientes() void
  + gerarArquivoClientes(String, int) void
  + main(String[]) void
  - gerarClienteFicticio() Cliente
}
class MergeSortExterno {
  + mergeSortExterno(String, String) void
  - divideEOrdenaBlocos(String) List~String~
  + ordenarEmMemoria(Cliente[]) void
  - mesclaBlocos(List~String~, String) void
}
class TabHashClientes {
  + create(Cliente) boolean
  + main(String[]) void
  + createOA(Cliente) boolean
  - hash0(String) int
  - hash3(String) int
  - hash4(String) int
  + hashLinear(String, int) int
  - hash5(String) int
  - hash6(String) int
  - hash(String) int
  + hashMultiplicacao(String, int) int
  - hash2(String) int
  - hashQuadratica(String, int, int, int) int
  - verificaDisponibilidade(int) boolean
  + testaEnderecamentoAberto(int) void
  + testaFuncaoHash(int) void
  - hash1(String) int
}
class TesteBufferClientes {
  + main(String[]) void
}

ArquivoCliente  ..>  ArquivoSequencial~T~ 
BufferDeClientes "1" *--> "arquivoSequencial 1" ArquivoSequencial~T~ 
BufferDeClientes  ..>  Buffer~T~ 
BufferDeClientes  ..>  Cliente : «create»
BufferDeClientes "1" *--> "buffer *" Cliente 
ClienteGUI  ..>  ArquivoCliente : «create»
ClienteGUI "1" *--> "bufferDeClientes 1" BufferDeClientes 
ClienteGUI  ..>  BufferDeClientes : «create»
ClienteGUI2  ..>  ArquivoCliente : «create»
ClienteGUI2 "1" *--> "bufferDeClientes 1" BufferDeClientes 
ClienteGUI2  ..>  BufferDeClientes : «create»
ClienteGUI2  ..>  Cliente : «create»
GeradorDeArquivosDeClientes "1" *--> "arquivoCliente 1" ArquivoCliente 
GeradorDeArquivosDeClientes  ..>  ArquivoCliente : «create»
GeradorDeArquivosDeClientes  ..>  Cliente : «create»
TabHashClientes  ..>  Cliente : «create»
TabHashClientes "1" *--> "tabHash *" Cliente 
TesteBufferClientes  ..>  ArquivoCliente : «create»
TesteBufferClientes  ..>  BufferDeClientes : «create»
TesteBufferClientes  ..>  Cliente : «create»
```