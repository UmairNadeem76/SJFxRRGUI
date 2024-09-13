package SJFxRRGUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

class Process {
    public String name;
    public int burstTime;
    public int priority;
    public int waitingTime;
    public int turnaroundTime;
    
    public Process(String name, int burstTime, int priority) {
        this.name = name;
        this.burstTime = burstTime;
        this.priority = priority;
        this.waitingTime = 0;
        this.turnaroundTime = 0;
    }
    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }
    public void setTurnaroundTime(int turnaroundTime) {
        this.turnaroundTime = turnaroundTime;
    }
}
public class SJFxRRGUI extends JFrame {
    private JTextArea outputTextArea;
    public SJFxRRGUI() {
        setTitle("SJF with Priority and Round-Robin Scheduler");
        setBounds(400, 150, 900, 450);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setBackground(Color.BLACK);
        outputTextArea.setForeground(Color.GREEN);

        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JButton runButton = new JButton("Start");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runScheduler();
            }
        });
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(runButton, BorderLayout.SOUTH);
    }
    private void runScheduler() {
        java.util.List <Process> yPriorityList = new ArrayList<>();
        java.util.List <Process> nPriorityList = new ArrayList<>();

        Scanner sc = new Scanner(System.in);
        int n = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter the number of processes:"));
        for (int i = 1; i <= n; i++) {
            int burstTime = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter the burst time of P" + i + ":"));

            String priorityString;
            while (true) {
                priorityString = JOptionPane.showInputDialog(null, "Enter the priority of P" + i + " (Y/N):").toUpperCase();
                if (priorityString.equals("Y") || priorityString.equals("N")) {
                    break;
                }
                JOptionPane.showMessageDialog(null, "Invalid priority! Please enter Y or N.");
            }

            if (priorityString.equals("Y")) {
                yPriorityList.add(new Process("P" + i, burstTime, 1));
            } else {
                nPriorityList.add(new Process("P" + i, burstTime, 0));
            }
        }
        // Sort the y and n priority lists in ascending order of burst time
        Collections.sort(yPriorityList, Comparator.comparingInt(p -> p.burstTime));
        Collections.sort(nPriorityList, Comparator.comparingInt(p -> p.burstTime));

        // Calculate the time quantum for Y priority processes as the median of the burst times
        float size = yPriorityList.size();
        float quantum;
        float sum = 0;
        if (size % 2 == 0) {
            int medianIndex = (yPriorityList.size()) / 2;
            sum = (yPriorityList.get(medianIndex).burstTime) + (yPriorityList.get(medianIndex - 1).burstTime);
            quantum = sum / 2;
        } else {
            int medianIndex = (yPriorityList.size() - 1) / 2;
            quantum = yPriorityList.get(medianIndex).burstTime;
        }
        // Run the Y priority processes first, using the calculated time quantum
        int time = 0;
        java.util.List<Process> allProcesses = new ArrayList<>();
        allProcesses.addAll(yPriorityList);
        allProcesses.addAll(nPriorityList);
        outputTextArea.append("Time Quantum for Priority Processes: " + quantum);
        outputTextArea.append("\n---GANTT CHART---\n");
        for (Process p : allProcesses) {
            if (p.priority == 1) { // Y priority
                int remainingTime = p.burstTime;
                while (remainingTime > 0) {
                    float runTime = Math.min(quantum, remainingTime);
                    time += runTime;
                    outputTextArea.append("|" + p.name + "| " + time + " ");
                    remainingTime -= runTime;
                }
                p.setWaitingTime(time - p.burstTime);
                p.setTurnaroundTime(p.waitingTime + p.burstTime);
            } else { // N priority
                outputTextArea.append("|" + p.name + "|" + (time + p.burstTime) + " ");
                p.setWaitingTime(time);
                time += p.burstTime;
                p.setTurnaroundTime(p.waitingTime + p.burstTime);
            }
        }
        outputTextArea.append("\n\n");
        // Calculate and print the waiting time and turnaround time for each process
        float avgWaitingTime = 0;
        float avgTurnaroundTime = 0;
        for (Process p : allProcesses) {
            avgWaitingTime += p.waitingTime;
            avgTurnaroundTime += p.burstTime + p.waitingTime;
            outputTextArea.append(p.name + "\t\tBurst Time: " + p.burstTime + "\t\tPriority: " + p.priority + "\t\tTurnaround Time:  " + p.turnaroundTime + "\t\tWaiting time: " + p.waitingTime + "\n");
        }
        avgWaitingTime /= n;
        avgTurnaroundTime /= n;
        outputTextArea.append("\n\nAverage Waiting time: " + avgWaitingTime + "\n");
        outputTextArea.append("Average Turnaround time: " + avgTurnaroundTime + "\n");
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SJFxRRGUI().setVisible(true);
            }
        });
    }
}