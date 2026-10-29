package software.services;

import software.entities.Categoria;
import software.entities.Produto;
import software.enums.TipoProduto;
import software.interfaces.ProdutoService;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class ProdutoServiceImpl implements ProdutoService {

    private final String arquivo = "produtos.txt";

    @Override
    public void cadastrar(Produto produto) {

        produto.setId(UUID.randomUUID());

        System.out.println("\nCADASTRO DE PRODUTO:\n");
        var scanner = new Scanner(System.in);

        System.out.print("INFORME O NOME........: ");
        produto.setNome(scanner.nextLine());

        System.out.print("INFORME O PREÇO.......: ");
        produto.setPreco(Double.parseDouble(scanner.nextLine()));

        System.out.print("INFORME A QUANTIDADE..: ");
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

        System.out.println("\nCONSULTA DE PRODUTOS:\n");

        var produtos = new ArrayList<Produto>();

        try (var scanner = new Scanner(new File(arquivo))){

            while (scanner.hasNextLine()) {

                var linha =  scanner.nextLine().trim();

                if (linha.startsWith("...")) linha = linha.substring(3).trim();
                if (linha.isEmpty()) continue;

                var produto = new Produto();
                produto.setCategoria(new Categoria());

                produto.setId(UUID.fromString(linha));
                produto.setNome(scanner.nextLine());
                produto.setPreco(Double.parseDouble(scanner.nextLine()));
                produto.setQuantidade(Integer.parseInt(scanner.nextLine()));
                produto.setTipo(TipoProduto.valueOf(scanner.nextLine()));
                produto.getCategoria().setId(UUID.fromString(scanner.nextLine()));
                produto.getCategoria().setNome(scanner.nextLine());
                produto.getCategoria().setDescricao(scanner.nextLine());

                produtos.add(produto);
            }

        } catch (Exception e) {
            System.out.println("\nERRO AO LER ARQUIVO: " + e.getMessage());
        }

        return produtos;
    }
}
