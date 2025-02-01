package qu;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

public class Quiz_Final 
{
	private JFrame fr;
    private JButton ub,nb,pb;
    private JLabel tl,sl,ql;
    private JRadioButton[] options;
    private ButtonGroup optgrp;
    private ArrayList<String[]> quizData;
    private ArrayList<String[]> report;
    private int ind=0, scr=0;
    private Timer qtime;
    private int leftime=30; 
    private boolean[] attempt; 


public Quiz_Final() 
{
    welcome();
}

private void welcome() 
{
    JFrame wfr=new JFrame("Welcome");
    wfr.getContentPane().setBackground(Color.BLACK);
    wfr.setSize(500,500);
    wfr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    wfr.setLayout(new BorderLayout());
    JLabel wlab=new JLabel("Welcome to the Quiz!", SwingConstants.CENTER);
    wlab.setFont(new Font("Times New Roman", Font.BOLD, 24));
    wlab.setForeground(Color.CYAN); 
    wfr.add(wlab, BorderLayout.CENTER);
    Timer timer = new Timer(5000, new ActionListener() 
    {
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            wfr.dispose();  
            start();             
        }
    });
    timer.setRepeats(false); 
    timer.start();
    wfr.setVisible(true);
}

private void start() 
{
    fr=new JFrame("Quiz Application");
    fr.setSize(600, 600);
    fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    fr.setLayout(new BorderLayout());
    
    ub=new JButton("Upload Quiz CSV");
    ub.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    ub.setBackground(Color.GREEN); 
    ub.setForeground(Color.BLACK); 
    ub.addActionListener(e->file());

    ql=new JLabel("Upload a CSV to start the quiz", SwingConstants.CENTER);
    ql.setFont(new Font("Times New Roman", Font.BOLD, 22));
    ql.setForeground(Color.CYAN); 

    options=new JRadioButton[4];
    optgrp=new ButtonGroup();
    JPanel optionsPanel=new JPanel(new GridLayout(4, 1));
    optionsPanel.setBackground(Color.BLACK);
    for(int i=0;i<4;i++) 
    {
        options[i]=new JRadioButton();
        options[i].setFont(new Font("Times New Roman", Font.PLAIN, 18)); 
        options[i].setForeground(Color.BLACK); 
        optgrp.add(options[i]);
        optionsPanel.add(options[i]);
    }

    nb=new JButton("Next Question");
    nb.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    nb.setBackground(Color.ORANGE); 
    nb.setForeground(Color.BLACK); 
    nb.addActionListener(e->checkAnswer());

    pb=new JButton("Previous Question");
    pb.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    pb.setBackground(Color.ORANGE); 
    pb.setForeground(Color.BLACK); 
    pb.addActionListener(e->goToPreviousQuestion());

    sl=new JLabel("", SwingConstants.CENTER);
    sl.setFont(new Font("Times New Roman", Font.BOLD, 14));
    sl.setForeground(Color.CYAN); 

    tl=new JLabel("Time Left: 30s", SwingConstants.CENTER);
    tl.setFont(new Font("Times New Roman", Font.BOLD, 14));
    tl.setForeground(Color.CYAN); 

    JPanel bPan=new JPanel(new FlowLayout());
    bPan.setBackground(Color.BLACK); 
    bPan.add(pb);
    bPan.add(nb);

    JPanel mPan=new JPanel(new BorderLayout());
    mPan.setBackground(Color.BLACK);
    mPan.add(ql, BorderLayout.NORTH);
    mPan.add(optionsPanel, BorderLayout.CENTER);
    mPan.add(tl, BorderLayout.SOUTH);

    fr.add(ub, BorderLayout.NORTH);
    fr.add(mPan, BorderLayout.CENTER);
    fr.add(bPan, BorderLayout.SOUTH);
    fr.add(sl, BorderLayout.EAST);

    fr.getContentPane().setBackground(Color.BLACK);
    fr.setVisible(true);
}

private void file() 
{
    JFileChooser fchoose=new JFileChooser();
    int val=fchoose.showOpenDialog(fr);
    if(val==JFileChooser.APPROVE_OPTION) 
    {
        File file=fchoose.getSelectedFile();
        quizData=new ArrayList<>();
        report=new ArrayList<>();
        attempt=new boolean[10];

        try(BufferedReader br=new BufferedReader(new FileReader(file))) 
        {
            String line;
            boolean fline=true;
            while((line=br.readLine())!=null) 
            {
                if(fline) 
                {
                    fline=false;
                    continue;
                }
                quizData.add(line.split(","));
            }
            System.out.println("Number of questions loaded: " + quizData.size());
            if (quizData.isEmpty()) 
            {
                JOptionPane.showMessageDialog(fr, "The uploaded file is empty or invalid.", "Error", JOptionPane.ERROR_MESSAGE);
            } 
            else 
            {
                ind = 0;
                scr = 0;
                show();
            }

        }
        catch(IOException e) 
        {
            JOptionPane.showMessageDialog(fr, "Error reading the file!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


private void show() 
{
    if (ind<quizData.size()) 
    {
        String[] qdata=quizData.get(ind);
        ql.setText("Q" + (ind + 1) + ": " + qdata[0]);
        for(int i=0;i<4;i++) 
        {
            options[i].setText(qdata[i+1]);
            options[i].setSelected(false);
        }
        leftime=30; 
        tl.setText("Time Left: " + leftime + "s");
        startTimer(); 
    } 
    else 
    {
        report();
    }
}

private void startTimer() 
{
    qtime=new Timer(1000, new ActionListener() 
    {
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            if(leftime>0) 
            {
                leftime--;
                tl.setText("Time Left: " + leftime + "s");
            } else 
            {
                qtime.stop(); 
                checkAnswer(); 
            }
        }
    });
    qtime.setRepeats(true); 
    qtime.start(); 
}

private void checkAnswer() 
{
    if(ind<quizData.size()) 
    {
        String[] qdata=quizData.get(ind);
        String correct=qdata[5].trim(); 
        String select="";

        for(int i=0;i<4;i++) 
        {
            if(options[i].isSelected()) 
            {
                select=String.valueOf((char)('A'+i));
                break;
            }
        }
        report.add(new String[]{qdata[0], select, correct});
        if(select.equals(correct)) 
        {
            scr++;
        }
        attempt[ind] = true;
        ind++;
        show();  
    } 
    else 
    {
        report();  
    }
}



private void goToPreviousQuestion() 
{
    if(ind>0) 
    {
        ind--;
        show();
    }
}

private void report() 
{
    int total=quizData.size();
    String str;
    double prcnt=(double)((scr/total)*100);

    StringBuilder rep=new StringBuilder("Quiz Report:\n\n");
    for(int i=0;i<quizData.size();i++) 
    {
        String[] reportRow=report.get(i);
        rep.append("Q").append(i + 1).append(": ").append(reportRow[0])
              .append("\nYour Answer: ").append(reportRow[1])
              .append("\nCorrect Answer: ").append(reportRow[2]).append("\n\n");
    }

    if (prcnt>=80) 
    {
        str="Excellent!";
    } 
    else if(prcnt>=50 && prcnt<80) 
    {
        str="Good Job!";
    } 
    else 
    {
        str="Keep Practicing!";
    }
    rep.append("Final Score: ").append(scr).append("/").append(total).append("\n");
    rep.append("Feedback: ").append(str);

    JOptionPane.showMessageDialog(fr, rep.toString(), "Quiz Completed", JOptionPane.INFORMATION_MESSAGE);
    System.exit(0);
}

public static void main(String[] args) 
{
    SwingUtilities.invokeLater(Quiz_Final::new);
}
}


