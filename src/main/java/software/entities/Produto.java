package software.entities;

import lombok.Data;
import software.enums.TipoProduto;

import java.util.UUID;

@Data
public class Produto {

    private UUID id;
    private String nome;
    private Double preco;
    private Integer quantidade;
    private TipoProduto tipo;
    private Categoria categoria;
}
