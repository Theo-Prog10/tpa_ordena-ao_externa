package entity.cms;

import java.util.*;
import java.io.*;

public class BufferDeClientes implements Buffer<Cliente> {

    private ArquivoSequencial<Cliente> arquivoSequencial;
    private Queue<Cliente> buffer;
    private final int TAMANHO_BUFFER = 10000;
    private String modo;
    private List<Cliente> bufferList;

    public BufferDeClientes() {
        this.buffer = new LinkedList<>();
        this.bufferList = new ArrayList<>();
    }

    @Override
    public void associaBuffer(ArquivoSequencial<Cliente> arquivoSequencial) {
        this.arquivoSequencial = arquivoSequencial;
    }

    @Override
    public void inicializaBuffer(String modo, String nomeArquivo) {
        this.modo = modo;
        try {
            if (modo.equals("leitura")) {
                arquivoSequencial.abrirArquivo(nomeArquivo, "leitura", Cliente.class);
            } else if (modo.equals("escrita")) {
                arquivoSequencial.abrirArquivo(nomeArquivo, "escrita", Cliente.class);
            } else {
                throw new IllegalArgumentException("Modo inválido: deve ser 'leitura' ou 'escrita'");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void carregaBuffer() {
        if (!modo.equals("leitura")) {
            throw new IllegalStateException("Buffer não está em modo de leitura!");
        }

        try {
            List<Cliente> clientesLidos = arquivoSequencial.leiaDoArquivo(TAMANHO_BUFFER);
            if (clientesLidos != null) {
                buffer.addAll(clientesLidos);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void escreveBuffer() {
        if (!modo.equals("escrita")) {
            throw new IllegalStateException("Buffer não está em modo de escrita!");
        }

        try {
            arquivoSequencial.escreveNoArquivo(new LinkedList<>(buffer));
            buffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fechaBuffer() {
        try {
            arquivoSequencial.fechaArquivo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Cliente proximoCliente() {
        if (!modo.equals("leitura")) {
            throw new IllegalStateException("Buffer não está em modo de leitura!");
        }

        if (buffer.isEmpty()) {
            carregaBuffer(); // Recarrega o buffer se estiver vazio
        }

        if (!buffer.isEmpty()) {
            return buffer.poll();
        }
        return null;
    }

    public Cliente[] proximosClientes(int quantidade) {
        Cliente[] clientes = new Cliente[quantidade];
        int i = 0;

        while (i < quantidade) {
            Cliente cliente = proximoCliente();
            if (cliente == null) {
                break;
            }
            clientes[i] = cliente;
            i++;
        }

        return Arrays.copyOf(clientes, i);
    }

    @Override
    public void adicionaAoBuffer(Cliente cliente) {
        if (cliente != null) {
            buffer.add(cliente); // Adiciona o cliente ao buffer
        }
    }

}
