package software.entities;

import lombok.Data;

import java.util.UUID;

@Data
public class Categoria {

    private UUID id;
    private String nome;
    private String descricao;
}
