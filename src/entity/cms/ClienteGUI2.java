package entity.cms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClienteGUI2 extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private BufferDeClientes bufferDeClientes;
    private final int TAMANHO_BUFFER = 10000;
    private int registrosCarregados = 0; // Contador de registros já carregados
    private String arquivoSelecionado;
    private boolean arquivoCarregado = false; // Para verificar se o arquivo foi carregado

    public ClienteGUI2() {
        setTitle("Gerenciamento de Clientes");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        bufferDeClientes = new BufferDeClientes();
        criarInterface();
    }

    private void carregarArquivo() {
        JFileChooser fileChooser = new JFileChooser();
        int retorno = fileChooser.showOpenDialog(this);
        if (retorno == JFileChooser.APPROVE_OPTION) {
            arquivoSelecionado = fileChooser.getSelectedFile().getAbsolutePath();
            bufferDeClientes.associaBuffer(new ArquivoCliente()); // Substitua por sua implementação
            bufferDeClientes.inicializaBuffer("leitura", arquivoSelecionado); // Passa o nome do arquivo aqui
            registrosCarregados = 0; // Reseta o contador
            tableModel.setRowCount(0); // Limpa a tabela
            carregarMaisClientes(); // Carrega os primeiros clientes
            arquivoCarregado = true; // Marca que o arquivo foi carregado
        }
    }

    private void criarInterface() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton btnCarregar = new JButton("Carregar Clientes");
        JButton btnPesquisar = new JButton("Pesquisar Cliente");
        JButton btnInserir = new JButton("Inserir Cliente");
        JButton btnRemover = new JButton("Remover Cliente"); // Botão para remover cliente
        tableModel = new DefaultTableModel(new String[]{"#", "Nome", "Sobrenome", "Telefone", "Endereço", "Credit Score"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Ação do botão "Carregar"
        btnCarregar.addActionListener(e -> carregarArquivo());

        // Ação do botão "Pesquisar"
        btnPesquisar.addActionListener(e -> {
            if (!arquivoCarregado) {
                JOptionPane.showMessageDialog(this, "Por favor, carregue um arquivo antes de pesquisar.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            pesquisarCliente();
        });

        // Ação do botão "Inserir"
        btnInserir.addActionListener(e -> {
            if (!arquivoCarregado) {
                JOptionPane.showMessageDialog(this, "Por favor, carregue um arquivo antes de inserir um cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            inserirCliente();
        });

        // Ação do botão "Remover"
        btnRemover.addActionListener(e -> {
            if (!arquivoCarregado) {
                JOptionPane.showMessageDialog(this, "Por favor, carregue um arquivo antes de remover um cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            removerCliente();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnCarregar);
        buttonPanel.add(btnPesquisar);
        buttonPanel.add(btnInserir);
        buttonPanel.add(btnRemover); // Adiciona o botão na interface

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        add(panel);
    }


    private void pesquisarCliente() {
        String nomePesquisa = JOptionPane.showInputDialog(this, "Digite o nome do cliente para pesquisar:");
        if (nomePesquisa == null || nomePesquisa.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        tableModel.setRowCount(0); // Limpa a tabela antes de exibir os resultados

        boolean encontrado = false;
        Cliente[] clientes;
        bufferDeClientes.inicializaBuffer("leitura", arquivoSelecionado); // Recarrega o buffer para pesquisar desde o início

        do {
            clientes = bufferDeClientes.proximosClientes(TAMANHO_BUFFER);
            for (Cliente cliente : clientes) {
                if (cliente != null && cliente.getNome().equalsIgnoreCase(nomePesquisa)) {
                    tableModel.addRow(new Object[]{tableModel.getRowCount() + 1, cliente.getNome(), cliente.getSobrenome(), cliente.getTelefone(), cliente.getEndereco(), cliente.getCreditScore()});
                    encontrado = true;
                }
            }
        } while (clientes.length > 0);

        if (!encontrado) {
            JOptionPane.showMessageDialog(this, "Cliente não encontrado!", "Resultado da Pesquisa", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void inserirCliente() {
        JPanel panel = new JPanel(new GridLayout(6, 2));
        JTextField nomeField = new JTextField();
        JTextField sobrenomeField = new JTextField();
        JTextField telefoneField = new JTextField();
        JTextField enderecoField = new JTextField();
        JTextField creditScoreField = new JTextField();

        panel.add(new JLabel("Nome:"));
        panel.add(nomeField);
        panel.add(new JLabel("Sobrenome:"));
        panel.add(sobrenomeField);
        panel.add(new JLabel("Telefone:"));
        panel.add(telefoneField);
        panel.add(new JLabel("Endereço:"));
        panel.add(enderecoField);
        panel.add(new JLabel("Credit Score:"));
        panel.add(creditScoreField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Inserir Novo Cliente", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String nome = nomeField.getText().trim();
                String sobrenome = sobrenomeField.getText().trim();
                String telefone = telefoneField.getText().trim();
                String endereco = enderecoField.getText().trim();
                int creditScore = Integer.parseInt(creditScoreField.getText().trim());

                Cliente novoCliente = new Cliente(nome, sobrenome, telefone, endereco, creditScore);

                // Inicializa o buffer em modo "leitura e escrita"
                bufferDeClientes.inicializaBuffer("leitura", arquivoSelecionado);

                // Lê todos os clientes existentes e mantém no buffer
                Cliente[] clientesExistentes = bufferDeClientes.proximosClientes(TAMANHO_BUFFER);

                // Reabre o buffer no modo "escrita" para sobrescrever o arquivo com os dados existentes + novo cliente
                bufferDeClientes.inicializaBuffer("escrita", arquivoSelecionado);

                // Adiciona os clientes existentes de volta ao arquivo
                for (Cliente cliente : clientesExistentes) {
                    if (cliente != null) {
                        bufferDeClientes.adicionaAoBuffer(cliente);
                    }
                }

                // Adiciona o novo cliente
                bufferDeClientes.adicionaAoBuffer(novoCliente);

                // Persiste no arquivo
                bufferDeClientes.escreveBuffer();

                JOptionPane.showMessageDialog(this, "Cliente inserido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarArquivo(); // Atualiza a tabela exibida
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Credit Score deve ser um número válido!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removerCliente() {
        // Solicita ao usuário o nome do cliente a ser removido
        String nomeRemover = JOptionPane.showInputDialog(this, "Digite o nome do cliente para remover:");
        if (nomeRemover == null || nomeRemover.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Inicializa o buffer no modo "leitura"
        bufferDeClientes.inicializaBuffer("leitura", arquivoSelecionado);

        // Lê todos os clientes do arquivo
        Cliente[] clientes = bufferDeClientes.proximosClientes(TAMANHO_BUFFER);
        List<Cliente> listaAtualizada = new ArrayList<>();

        boolean clienteRemovido = false;

        // Filtra os clientes para excluir aquele com o nome especificado
        for (Cliente cliente : clientes) {
            if (cliente != null && !cliente.getNome().equalsIgnoreCase(nomeRemover)) {
                listaAtualizada.add(cliente);
            } else if (cliente != null && cliente.getNome().equalsIgnoreCase(nomeRemover)) {
                clienteRemovido = true;
            }
        }

        // Verifica se o cliente foi encontrado e removido
        if (!clienteRemovido) {
            JOptionPane.showMessageDialog(this, "Cliente não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Reabre o buffer no modo "escrita"
        bufferDeClientes.inicializaBuffer("escrita", arquivoSelecionado);

        // Adiciona os clientes atualizados de volta ao buffer
        for (Cliente cliente : listaAtualizada) {
            bufferDeClientes.adicionaAoBuffer(cliente);
        }

        // Persiste os dados atualizados no arquivo
        bufferDeClientes.escreveBuffer();

        // Atualiza a tabela exibida
        carregarArquivo();

        JOptionPane.showMessageDialog(this, "Cliente removido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }



    private void carregarMaisClientes() {
        // Carrega apenas 10.000 registros de cada vez
        Cliente[] clientes = bufferDeClientes.proximosClientes(TAMANHO_BUFFER); // Chama o método com o tamanho do buffer
        if (clientes != null && clientes.length > 0) {
            // Ordena os clientes usando o Merge Sort Externo
            MergeSortExterno.ordenarEmMemoria(clientes);


            // Exibe os clientes ordenados na tabela
            for (Cliente cliente : clientes) {
                if (cliente != null) { // Verifica se o cliente não é nulo
                    tableModel.addRow(new Object[]{tableModel.getRowCount() + 1, cliente.getNome(), cliente.getSobrenome(), cliente.getTelefone(), cliente.getEndereco(), cliente.getCreditScore()});
                }
            }
            registrosCarregados += clientes.length; // Atualiza o contador
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClienteGUI2 gui = new ClienteGUI2();
            gui.setVisible(true);
        });
    }
}
