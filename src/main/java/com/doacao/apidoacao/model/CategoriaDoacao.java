package com.doacao.apidoacao.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categorias_doacao")
public class CategoriaDoacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategoria;

    @Column(nullable = false, unique = true, length = 50)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    public Long getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Long idCategoria) { this.idCategoria = idCategoria; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
