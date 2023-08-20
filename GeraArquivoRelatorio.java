/*
 * Diego Tamiozzo
 * Processamento de dados e geração de um relatório com base em registros de acionamento e desligamento de um motor.
 * 20/08/2023
*/
package Performance_motor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class GeraArquivoRelatorio {
    private static int tempoTotalLigado = 0;
    private static int somaMediasTempo = 0;
    private static int contadorCiclosTotal = 0;

    public static void main(String[] args) {
        Map<String, List<String>> dadosAcionadosPorDia = new HashMap<>();
        Map<String, List<String>> dadosDesligadosPorDia = new HashMap<>();

        lerDadosDoArquivo("C:\\Users\\user pc\\Desktop\\dados\\dados01.txt", "Entrada 1 Acionada", dadosAcionadosPorDia);
        lerDadosDoArquivo("C:\\Users\\user pc\\Desktop\\dados\\dados02.txt", "Entrada 1 Desligada", dadosDesligadosPorDia);

        calcularDiferencasEImprimirRelatoriosOrdenados(dadosAcionadosPorDia, dadosDesligadosPorDia);

        imprimirRelatorioFinal();
    }

    private static void imprimirRelatorioFinal() {
		
	}

	private static void lerDadosDoArquivo(String filePath, String targetString, Map<String, List<String>> dadosPorDia) {
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String linha = scanner.nextLine();

                if (linha.contains(targetString)) {
                    String[] dados = extrairDados(linha);
                    if (dados != null) {
                        String dia = dados[0];
                        dadosPorDia.computeIfAbsent(dia, k -> new ArrayList<>()).add(dados[1]);
                    }
                }
            }

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String[] extrairDados(String linha) {
        String[] dados = linha.split(",");
        if (dados.length >= 4) {
            String[] dadosFormatados = new String[2];
            dadosFormatados[0] = dados[1].trim();
            dadosFormatados[1] = dados[3].trim();
            return dadosFormatados;
        }
        return null;
    }

    private static void calcularDiferencasEImprimirRelatoriosOrdenados(Map<String, List<String>> dadosAcionadosPorDia, Map<String, List<String>> dadosDesligadosPorDia) {
        HashMap<String, List<String[]>> matrizesOrdenadas = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : dadosDesligadosPorDia.entrySet()) {
            String dia = entry.getKey();
            List<String> horariosDesligados = entry.getValue();
            List<String> horariosAcionados = dadosAcionadosPorDia.get(dia);

            if (horariosAcionados != null) {
                List<String[]> matrizDiferencas = new ArrayList<>();
                for (int i = 0; i < horariosDesligados.size(); i++) {
                    String horarioDesligado = horariosDesligados.get(i);
                    String horarioAcionado = horariosAcionados.get(i);

                    int diff = diferencaHorarios(horarioAcionado, horarioDesligado);
                    String[] linhaMatriz = {String.valueOf(i + 1), horarioAcionado, horarioDesligado, String.valueOf(diff)};
                    matrizDiferencas.add(linhaMatriz);
                }

                matrizesOrdenadas.put(dia, matrizDiferencas);
            }
        }

        List<String> datasOrdenadas = new ArrayList<>(matrizesOrdenadas.keySet());
        Collections.sort(datasOrdenadas);

        try {
            FileWriter writer = new FileWriter("C:\\Users\\user pc\\Desktop\\dados\\relatorio.txt");

            for (String dia : datasOrdenadas) {
                List<String[]> matrizDiferencas = matrizesOrdenadas.get(dia);
                imprimirRelatorioPorDia(matrizDiferencas, dia, writer);

                for (String[] linha : matrizDiferencas) {
                    int duracao = Integer.parseInt(linha[3]);
                    tempoTotalLigado += duracao;
                    somaMediasTempo += duracao;
                }

                contadorCiclosTotal += matrizDiferencas.size();
            }

            imprimirRelatorioFinal(writer);

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int diferencaHorarios(String hora1, String hora2) {
        String[] horaMinutoSegundo1 = hora1.split(":");
        String[] horaMinutoSegundo2 = hora2.split(":");

        int hora1Int = Integer.parseInt(horaMinutoSegundo1[0]);
        int minuto1Int = Integer.parseInt(horaMinutoSegundo1[1]);
        int segundo1Int = Integer.parseInt(horaMinutoSegundo1[2]);

        int hora2Int = Integer.parseInt(horaMinutoSegundo2[0]);
        int minuto2Int = Integer.parseInt(horaMinutoSegundo2[1]);
        int segundo2Int = Integer.parseInt(horaMinutoSegundo2[2]);

        int totalSegundos1 = hora1Int * 3600 + minuto1Int * 60 + segundo1Int;
        int totalSegundos2 = hora2Int * 3600 + minuto2Int * 60 + segundo2Int;

        return totalSegundos2 - totalSegundos1;
    }

    private static void imprimirRelatorioPorDia(List<String[]> matriz, String dia, FileWriter writer) throws IOException {
        writer.write("Data: " + dia + "\n");
        int somaTotal = 0;
        int contadorCiclos = 0;
        List<String[]> maioresTempos = new ArrayList<>();
        List<String[]> menoresTempos = new ArrayList<>();

        for (String[] linha : matriz) {
            int duracao = Integer.parseInt(linha[3]);
            somaTotal += duracao;

            if (maioresTempos.size() < 5 || duracao > Integer.parseInt(maioresTempos.get(0)[3])) {
                maioresTempos.add(linha);
                maioresTempos.sort(Comparator.comparingInt(l -> Integer.parseInt(l[3])));
                if (maioresTempos.size() > 5) {
                    maioresTempos.remove(0);
                }
            }

            if (menoresTempos.size() < 5 || duracao < Integer.parseInt(menoresTempos.get(menoresTempos.size() - 1)[3])) {
                menoresTempos.add(linha);
                menoresTempos.sort(Comparator.comparingInt(l -> Integer.parseInt(l[3])));
                if (menoresTempos.size() > 5) {
                    menoresTempos.remove(menoresTempos.size() - 1);
                }
            }

            contadorCiclos = Integer.parseInt(linha[0]);
        }

        int somaTotalHoras = somaTotal / 3600;
        int somaTotalMinutos = (somaTotal % 3600) / 60;
        int mediaDuracaoMinutos = somaTotal / (matriz.size() * 60);

        DecimalFormat decimalFormat = new DecimalFormat("00");
        writer.write("Tempo Total Ligado: " + decimalFormat.format(somaTotalHoras) + " horas e " + decimalFormat.format(somaTotalMinutos) + " minutos\n");
        writer.write("Média de Tempo: " + decimalFormat.format(mediaDuracaoMinutos) + " minutos\n");

        writer.write("Maiores Tempos: \n");
        for (String[] linha : maioresTempos) {
            writer.write(String.join(" ", linha) + "\n");
        }

        writer.write("Menores Tempos: \n");
        for (String[] linha : menoresTempos) {
            writer.write(String.join(" ", linha) + "\n");
        }

        writer.write("Contador de Ciclos: " + contadorCiclos + "\n\n");
    }

    private static void imprimirRelatorioFinal(FileWriter writer) throws IOException {
        int mediaTempoFinal = somaMediasTempo / contadorCiclosTotal;

        writer.write("RELATÓRIO FINAL:\n");
        writer.write("Tempo Total Ligado: " + formatarTempo(tempoTotalLigado) + "\n");
        writer.write("Média de Tempo: " + formatarTempo(mediaTempoFinal) + "\n");
        writer.write("Contador de Ciclos: " + contadorCiclosTotal + "\n");

        writer.close();
    }

    private static String formatarTempo(int totalSegundos) {
        int totalHoras = totalSegundos / 3600;
        int totalMinutos = (totalSegundos % 3600) / 60;
        DecimalFormat decimalFormat = new DecimalFormat("00");
        return decimalFormat.format(totalHoras) + " horas e " + decimalFormat.format(totalMinutos) + " minutos";
    }
}
