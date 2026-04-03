package com.doacao.apidoacao.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ongs")
public class Ong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOng;

    @Column(nullable = false, length = 150)
    private String razaoSocial;

    @Column(length = 120)
    private String nomeFantasia;

    @Column(unique = true, length = 20)
    private String cnpj;

    @Column(length = 120)
    private String email;

    @Column(length = 20)
    private String telefone;

    @Column(length = 120)
    private String responsavel;

    @Column(length = 150)
    private String endereco;

    @Column(length = 20)
    private String numero;

    @Column(length = 80)
    private String complemento;

    @Column(length = 80)
    private String bairro;

    @Column(length = 80)
    private String cidade;

    @Column(length = 2)
    private String estado;

    @Column(length = 10)
    private String cep;

    @Column(length = 100)
    private String areaAtuacao;

    @Column(length = 20)
    private String statusOng;

    private LocalDateTime dataCadastro;

    @PrePersist
    public void prePersist() {
        this.dataCadastro = LocalDateTime.now();
        if (this.statusOng == null || this.statusOng.isBlank()) {
            this.statusOng = "ativa";
        }
    }

    public Long getIdOng() {
        return idOng;
    }

    public void setIdOng(Long idOng) {
        this.idOng = idOng;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getAreaAtuacao() {
        return areaAtuacao;
    }

    public void setAreaAtuacao(String areaAtuacao) {
        this.areaAtuacao = areaAtuacao;
    }

    public String getStatusOng() {
        return statusOng;
    }

    public void setStatusOng(String statusOng) {
        this.statusOng = statusOng;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}