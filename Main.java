import com.sun.jna.Library;
import com.sun.jna.Native;

import java.util.Scanner;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.FileInputStream;

public class Main {

    // Interface que representa a DLL, usando JNA
    public interface ImpressoraDLL extends Library {

        // Caminho completo para a DLL
        ImpressoraDLL INSTANCE = (ImpressoraDLL) Native.load(
                "C:\\Users\\andrade_bruno\\Downloads\\Java-Aluno Graduacao\\E1_Impressora01.dll",
                ImpressoraDLL.class
        );


        private static String lerArquivoComoString(String path) throws IOException {
            FileInputStream fis = new FileInputStream(path);
            byte[] data = fis.readAllBytes();
            fis.close();
            return new String(data, StandardCharsets.UTF_8);
        }


        int AbreConexaoImpressora(int tipo, String modelo, String conexao, int param);

        int FechaConexaoImpressora();

        int ImpressaoTexto(String dados, int posicao, int estilo, int tamanho);

        int Corte(int avanco);

        int ImpressaoQRCode(String dados, int tamanho, int nivelCorrecao);

        int ImpressaoCodigoBarras(int tipo, String dados, int altura, int largura, int HRI);

        int AvancaPapel(int linhas);

        int StatusImpressora(int param);

        int AbreGavetaElgin();

        int AbreGaveta(int pino, int ti, int tf);

        int SinalSonoro(int qtd, int tempoInicio, int tempoFim);

        int ModoPagina();

        int LimpaBufferModoPagina();

        int ImprimeModoPagina();

        int ModoPadrao();

        int PosicaoImpressaoHorizontal(int posicao);

        int PosicaoImpressaoVertical(int posicao);

        int ImprimeXMLSAT(String dados, int param);

        int ImprimeXMLCancelamentoSAT(String dados, String assQRCode, int param);
    }

    private static boolean conexaoAberta = false; // Obviamente ele tem que iniciar com falso (false) pois ele que vai iniciar todo o programa
    private static int tipo;
    private static String modelo;
    private static String conexao;
    private static int parametro;

    public static void setConexaoAberta(boolean conexaoAberta) {
        Main.conexaoAberta = conexaoAberta;
    }

    private static final Scanner scanner = new Scanner(System.in);

    private static String capturarEntrada(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine();
    }

    public static void configurarConexao() {
        if (!conexaoAberta) {
            System.out.println("\n--- CONFIGURAR CONEXÃO ---");
            tipo = Integer.parseInt(capturarEntrada("Digite o tipo de conexão (1-USB, 2-TCP/IP, 4-Bluetooth): " +
                    "(Android)):"));
            modelo = capturarEntrada("Digite o modelo da impressora (i7, i8, i9): ");
            conexao = capturarEntrada("Digite a conexão (USB, TCP/IP, Bluetooth): ");
            parametro = Integer.parseInt(capturarEntrada("Digite o parâmetro (padrão 0): "));

            System.out.println("Configuração salva!");
        } else {
            System.out.println("Conexão já está aberta!");
        }
    }

    public static void abrirConexao() {
        if (conexaoAberta) {
            // Aqui como eu só coloquei ConexãoAberta ele ta vendo se é verdadeira(true) então não precisa coloca conexãoAberta == True apenas o conexãoAberta ja serve
            System.out.println("Conexão já está aberta!");
            return;
        }

        int retorno = ImpressoraDLL.INSTANCE.AbreConexaoImpressora(tipo, modelo, conexao, parametro);
        // Esse retorno eu usei pra ver se a AbreConexãoImpressora está preenchido, se tiver ele vai comparar
        if (retorno == 0) {
            // O '0' seguinifica verdadeiro, então se tiver preenchido ele vai ler e vai falar q é verdadeiro, então ele vai deixar a conexaoAberta ativada então de 'false' fica 'true'.
            conexaoAberta = true;
            System.out.println("Conexão com a impressora aberta com sucesso!");
        } else {
            System.out.println("Falha ao abrir conexão. Código de erro: " + retorno);
        }
    }

    public static void fecharConexao() {
        // Aqui ele vai ver se a 'conexãoaberta' é diferente de 'true' se for ele vai aparecer a mensagem do if, se não for diferente de true, ele vai fechar a conexão.
        if (!conexaoAberta) {
            System.out.println("Nenhuma conexão aberta.");
            return;
        }
        ImpressoraDLL.INSTANCE.FechaConexaoImpressora();
        conexaoAberta = false;
        System.out.println("Conexão com a impressora foi fechada.");
    }

    public static void main(String[] args) throws IOException {
        while (true) {
            System.out.println("\n*****************");
            System.out.println("****** MENU IMPRESSORA *******");
            System.out.println("*****************\n");

            System.out.println("1  - Configurar Conexao");
            System.out.println("2  - Abrir Conexao");
            System.out.println("3  - Impressao Texto");
            System.out.println("4  - Impressao QRCode");
            System.out.println("5  - Impressao Cod Barras");
            System.out.println("6  - Impressao XML SAT");
            System.out.println("7  - Impressao XML Canc SAT");
            System.out.println("8  - Abrir Gaveta Elgin");
            System.out.println("9  - Abrir Gaveta");
            System.out.println("10 - Sinal Sonoro");
            System.out.println("0  - Fechar Conexao e Sair");
            System.out.println("--------------------------------------");

            String escolha = capturarEntrada("\nDigite a opção desejada: ");

            if (escolha.equals("0")) {
                fecharConexao();
                System.out.println("Saindo...");
                return;
            }

            switch (escolha) {
                case "1": // puxando o metodo "Configurar Conexão"
                    configurarConexao();
                    break;
                case "2": // puxando o metodo "Abrir Conexão"
                    abrirConexao();
                    break;
                case "3": // Impressão do texto do Cupom
                    if (conexaoAberta) {
                        //ConexãoAberta é a variavel que foi criada com private boolean recebendo false, e aqui esta vendo se ela é true ou false
                        String msg = capturarEntrada("Digite o texto para imprimir: ");
                        ImpressoraDLL.INSTANCE.ImpressaoTexto(msg, 1, 4, 0);
                        ImpressoraDLL.INSTANCE.Corte(2);
                    } else {
                        System.out.println("Abre a conexão primeiro!");
                    }
                    break;
                case "4": // Impressão do QRCode
                    if (conexaoAberta) {
                        String qr = capturarEntrada("Digite o conteúdo do QR Code: ");
                        ImpressoraDLL.INSTANCE.ImpressaoQRCode(qr, 6, 4);
                        ImpressoraDLL.INSTANCE.Corte(2);
                    } else {
                        System.out.println("Abra a conexão primeiro!");
                    }
                    break;
                case "5": // Impressão Código de Barras
                    if (conexaoAberta) {
                        try {
                            // O try ele ajuda a não deixar o codigo finalizar caso de algum erro, ele é tipo um Tente executar esse codigo.
                            int tipoCodigo = Integer.parseInt(capturarEntrada("Tipo do código (Use: 8 = CODE 128)")); // {A012345678912
                            String dadosCod = capturarEntrada("Dados do código de barra (somente números/permitido conforme tipo): ");
                            int altura = Integer.parseInt(capturarEntrada("Altura (1 até 255, coloque 100): "));
                            int largura = Integer.parseInt(capturarEntrada("Largura (1 até 6), coloque 2: "));
                            int hri = Integer.parseInt(capturarEntrada("HRI (1 = Acima do codigo, 2 = Abaixo do codigo, 3 = Ambos, 4 = Não impresso.), coloque 3:"));
                            ImpressoraDLL.INSTANCE.ImpressaoCodigoBarras(tipoCodigo, dadosCod, altura, largura, hri);
                            ImpressoraDLL.INSTANCE.Corte(2);
                        } catch (NumberFormatException e) {
                            // O Catch é para caso se der erro mostrar alguma coisa, por exemplo uma mensagem igual a baixo
                            System.out.println("Entrada numérica inválida. Operação cancelada.");
                        }
                    } else {
                        System.out.println("Abra a conexão primeiro!");
                    }
                    break;
                case "6": // Impressão XML SAT
                    if (conexaoAberta) {
                        JFileChooser chooser = new JFileChooser();
                        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                            File xml = chooser.getSelectedFile();
                            try {
                                String conteudo = ImpressoraDLL.lerArquivoComoString(xml.getAbsolutePath());
                                ImpressoraDLL.INSTANCE.ImprimeXMLSAT(conteudo, 0);
                            } catch (IOException e) {
                                System.out.println("Erro lendo o arquivo: " + e.getMessage());
                            }

                        }
                    } else {
                        System.out.println("Abre a conexão primeiro!");
                    }
                    break;

                case "7": //Impressão XML Cancelamento SAT
                    if (conexaoAberta) {
                        JFileChooser chooser2 = new JFileChooser();
                        if (chooser2.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                            File xmlCanc = chooser2.getSelectedFile();
                            try {
                                String conteudoCanc = ImpressoraDLL.lerArquivoComoString(xmlCanc.getAbsolutePath());

                                String assQRCode = capturarEntrada("Digite a assinatura do QRCode (assQRCode) ou deixe em branco se não tiver: ");
                                ImpressoraDLL.INSTANCE.ImprimeXMLCancelamentoSAT(conteudoCanc, assQRCode, 0);
                            } catch (IOException e) {
                                System.out.println("Erro lendo o arquivo: " + e.getMessage());
                            }
                        }
                    } else {
                        System.out.println("Abre a conexão primeiro!");
                    }
                    break;
                case "8": //Abrir Gaveta Elgin (Comando simples)
                    if (conexaoAberta) {
                        int ret = ImpressoraDLL.INSTANCE.AbreGavetaElgin();
                        System.out.println("Comando abrir gaveta Elgin enviado. Código retorno: " + ret);
                    } else {
                        System.out.println("Abra a conexão primeiro!");
                    }
                    break;
                case "9": //Abrir Gaveta com parâmentros
                    if (conexaoAberta) {
                        try {
                            int pino = Integer.parseInt(capturarEntrada("Pino (0 ou 1): "));
                            int ti = Integer.parseInt(capturarEntrada("Tempo de pulso INICIO (ms): "));
                            int tf = Integer.parseInt(capturarEntrada("Tempo de pulso FIM (ms): "));
                            int retG = ImpressoraDLL.INSTANCE.AbreGaveta(pino, ti, tf);
                            System.out.println("Comando abrir gaveta enviado. Código retorno: " + retG);
                        } catch (NumberFormatException e) {
                            System.out.println("Entrada numérica inválida. Operação cancelada.");
                        }
                    } else {
                        System.out.println("Abra a conexão primeiro!");
                    }
                    break;
                case "10": // Sinal Sonoro
                    if (conexaoAberta) {
                        try {
                            int qtd = Integer.parseInt(capturarEntrada("Quantidade de sinais: "));
                            int tInicio = Integer.parseInt(capturarEntrada("Tempo início (ms): "));
                            int tFim = Integer.parseInt(capturarEntrada("Tempo fim (ms): "));
                            ImpressoraDLL.INSTANCE.SinalSonoro(qtd, tInicio, tFim);
                        } catch (NumberFormatException e) {
                            System.out.println("Entrada numérica inválida. Operação cancelada.");
                        }
                    } else {
                        System.out.println("Abra a conexão primeiro!");
                    }
                    break;
                default:
                    System.out.println("Opção invalida!");
                    break;
            }
        }
    }
}