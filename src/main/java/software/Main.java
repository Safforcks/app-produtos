package software;

import software.entities.Categoria;
import software.entities.Produto;
import software.services.CategoriaServiceImpl;
import software.services.ProdutoServiceImpl;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);

        System.out.println("OPÇÕES DE MENU:\n");
        System.out.println("\t(1) CADASTRAR CATEGORIA");
        System.out.println("\t(2) CONSULTAR CATEGORIAS");
        System.out.println("\t(3) CADASTRAR PRODUTOS");
        System.out.println("\t(4) CONSULTAR PRODUTOS");


        System.out.print("\nINFORME A OPÇÃO DESEJADA: ");
        var opcao = Integer.parseInt(scanner.nextLine());

        var categoriaService = new CategoriaServiceImpl();
        var produtoService = new ProdutoServiceImpl();

        switch (opcao) {
            case 1:
                categoriaService.cadastrar(new Categoria());
                break;
            case 2:
                var categorias = categoriaService.consultar();

                for (var c: categorias) {
                    System.out.println("...");
                    System.out.println("ID.........: " + c.getId());
                    System.out.println("NOME.......: " + c.getNome());
                    System.out.println("DESCRIÇÃO..: " + c.getDescricao());
                }

                break;
            case 3:
                var produto = new Produto();

                produto.setCategoria(new Categoria());
                produtoService.cadastrar(produto);
                break;
            case 4:
                var produtos = produtoService.consultar();

                for (var p: produtos) {
                    System.out.println("...");
                    System.out.println("ID.........: " + p.getId());
                    System.out.println("NOME.......: " + p.getNome());
                    System.out.println("PREÇO.....: " + p.getPreco());
                    System.out.println("QTD........: " + p.getQuantidade());
                    System.out.println("TIPO.......: " + p.getTipo());
                    System.out.println("CATEGORIA..: " + p.getCategoria().getNome());
                    System.out.println("DESCRIÇÃO..: " + p.getCategoria().getDescricao());
                }
                break;
            default:
                System.out.println("\nOPÇÃO INVÁLIDO!");
        }
    }
}