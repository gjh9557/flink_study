import akka.stream.impl.fusing.Scan;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner sc=new Scanner(System.in);
        String read[]=sc.nextLine().split(" ");
        int n=Integer.parseInt(read[0]);
        if(n<1||n>50){return ;}
        int [][]tmp1=new int[2][n];
        int [][]tmp2=new int[2][n];
        String num1[]=sc.nextLine().split("");
        String num2[]=sc.nextLine().split("");
        for (int i=0;i<n;i++){

            if(num1[i].equals("x")){
                tmp2[0][i]=1;
            }
            else {tmp2[0][i]=0;}
            if(num2[i].equals("x")){
                tmp1[1][i]=1;
            }
            else {tmp1[1][i]=0;}
        }
        if(tmp2[0][0]==1){
            return;
        }
        tmp1[0][0]=1;
        for(int i=0;i<2;i++){
            for(int j=0;j<2;j++){
                if(i==0||j==0){
                    if(tmp2[i][j]==0){
                        tmp1[i][j]=1;
                    }else{if(tmp2[i][j]==0){
                        tmp1[i][j]=tmp1[i-1][j]+tmp1[i][j-1]+tmp1[i-1][j-1];
                    }}
                }

            }
            }
        System.out.println(tmp1[1][n-1]);
    }
    void test(){
        Scanner input=new Scanner(System.in);
        String a=input.nextLine();
        System.out.println("输入数字");
        String c[]=a.split(" ");
        int num=Integer.parseInt(c[0]);
        int cal=Integer.parseInt(c[1]);
        System.out.println("输入测试数字");

        String b=input.nextLine();
        String read[]=b.split(" ");
        int result[]=new int[1000];
        for (String tmp:read){
            int tmp_num=Integer.parseInt(tmp);
            result[cal|tmp_num]++;
        }
        int max=0;
        int co= (int) 1e9;

        for (int tmp :
                result) {
            if(tmp>max){
                max=tmp;
            }
        }
        System.out.println(max);


    }
}
