package software;

import software.entities.Categoria;
import software.services.CategoriaServiceImpl;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);

        System.out.println("OPÇÕES DE MENU:\n");
        System.out.println("\t(1) CADASTRAR CATEGORIA");
        System.out.println("\t(2) CONSULTAR CATEGORIAS");

        System.out.print("\nINFORME A OPÇÃO DESEJADA: ");
        var opcao = Integer.parseInt(scanner.nextLine());

        var categoriaService = new CategoriaServiceImpl();

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
            default:
                System.out.println("\nOPÇÃO INVÁLIDO!");
        }
    }
}