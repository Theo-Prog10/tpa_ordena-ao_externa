package entity.cms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
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
        JButton btnPesquisar = new JButton("Pesquisar Cliente"); // Botão de pesquisa
        tableModel = new DefaultTableModel(new String[]{"#", "Nome", "Sobrenome", "Telefone", "Endereço", "Credit Score"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Listener para rolagem
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            if (!scrollPane.getVerticalScrollBar().getValueIsAdjusting()) {
                if (arquivoCarregado &&
                        scrollPane.getVerticalScrollBar().getValue() +
                                scrollPane.getVerticalScrollBar().getVisibleAmount() >=
                                scrollPane.getVerticalScrollBar().getMaximum()) {
                    carregarMaisClientes();
                }
            }
        });

        // Ação do botão de carregar arquivo
        btnCarregar.addActionListener(e -> carregarArquivo());

        // Ação do botão de pesquisar
        btnPesquisar.addActionListener(e -> {
            if (!arquivoCarregado) {
                JOptionPane.showMessageDialog(this, "Por favor, carregue um arquivo antes de pesquisar.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            pesquisarCliente(); // Chama o método de pesquisa
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnCarregar);
        buttonPanel.add(btnPesquisar);

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
