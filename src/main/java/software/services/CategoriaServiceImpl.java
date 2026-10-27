package software.services;

import software.entities.Categoria;
import software.interfaces.CategoriaService;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class CategoriaServiceImpl implements CategoriaService {

    private final String arquivo = "categoria.txt";

    @Override
    public void cadastrar(Categoria entidade) {

        var categoria = new Categoria();
        categoria.setId(UUID.randomUUID());

        var scanner = new Scanner(System.in);

        System.out.println("\nCADASTRO DE CATEGORIA:\n");

        System.out.print("INFORME O NOME........: ");
        categoria.setNome(scanner.nextLine());

        System.out.print("INFORME A DESCRIÇÃO...: ");
        categoria.setDescricao(scanner.nextLine());

        try (var printWriter = new PrintWriter(new FileWriter(arquivo, true))) {

            printWriter.write(categoria.getId() + "\n");
            printWriter.write(categoria.getNome() + "\n");
            printWriter.write(categoria.getDescricao() + "\n");
            printWriter.write("...");

        } catch (Exception e) {
            System.out.println("\nFALHA AO GRAVAR O ARQUIVO: " +e.getMessage());
        }

    }

    @Override
    public List<Categoria> consultar() {
        return List.of();
    }
}
