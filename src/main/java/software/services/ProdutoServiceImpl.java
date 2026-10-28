package software.services;

import software.entities.Produto;
import software.enums.TipoProduto;
import software.interfaces.ProdutoService;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class ProdutoServiceImpl implements ProdutoService {

    private final String arquivo = "produtos.txt";

    @Override
    public void cadastrar(Produto entidade) {


        var produto = new Produto();
        produto.setId(UUID.randomUUID());

        System.out.println("\nCADASTRO DE PRODUTO:\n");
        var scanner = new Scanner(System.in);

        System.out.println("INFORME O NOME........: ");
        produto.setNome(scanner.nextLine());

        System.out.println("INFORME O PREÇO.......: ");
        produto.setPreco(Double.parseDouble(scanner.nextLine()));

        System.out.println("INFORME A QUANTIDADE..: ");
        produto.setQuantidade(Integer.parseInt(scanner.nextLine()));

        System.out.println("TIPO DE PRODUTO:");
        System.out.println("\t(1) PERECÍVEL");
        System.out.println("\t(2) NÃO PERECÍVEL");
        System.out.println("\t(3) DIGITAL");
        System.out.print("INFORME O TIPO: ");

        switch (Integer.parseInt(scanner.nextLine())) {
            case 1: produto.setTipo(TipoProduto.PERECIVEL); break;
            case 2: produto.setTipo(TipoProduto.NAO_PERECIVEL); break;
            case 3: produto.setTipo(TipoProduto.DIGITAL); break;
        }

        produto.getCategoria().setId(UUID.randomUUID());

        System.out.print("NOME DA CATEGORIA.......: ");
        produto.getCategoria().setNome(scanner.nextLine());

        System.out.print("DESCRIÇÃO DA CATEGORIA..: ");
        produto.getCategoria().setDescricao(scanner.nextLine());

        try (var printWriter = new PrintWriter(new FileWriter(arquivo, true))) {

            printWriter.write(produto.getId() + "\n");
            printWriter.write(produto.getNome() + "\n");
            printWriter.write(produto.getPreco() + "\n");
            printWriter.write(produto.getQuantidade() + "\n");
            printWriter.write(produto.getTipo() + "\n");
            printWriter.write(produto.getCategoria().getId() + "\n");
            printWriter.write(produto.getCategoria().getNome() + "\n");
            printWriter.write(produto.getCategoria().getDescricao() + "\n");
            printWriter.write("...");

            System.out.println("\nDADOS GRAVADOS COM SUCESSO!");
        } catch (Exception e) {
            System.out.println("\nERRO AO GRAVAR ARQUIVO: " + e.getMessage());
        }
    }

    @Override
    public List<Produto> consultar() {
        return List.of();
    }
}
