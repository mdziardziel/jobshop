import java.io.*;
import java.util.ArrayList;
import java.lang.*;
import java.util.Random;

class Row {
  Row(int time, int machine, Row earlRow){
    this.earlRow = earlRow;
    if(earlRow != null)
    earlRow.nextRow = this;
    this.time = time;
    this.machine = machine;
  }
  public int time;
  public int machine;
  public int start;
  public Row nextRow;
  public Row earlRow;
  public void setStart(int start){
    this.start = start;
  }
  public int getTime(){
    return time;
  }
  public int pozCzasu(){
    if(nextRow !=null){
    Row r = this;
    int timee = 0;
    while(r.getNextRow()!=null){
      timee+=r.time;
      r = r.getNextRow();
    }
    timee+=r.time;
    return timee;
  } return this.time;
  }
  public int getStart(){
    return start;
  }
  public int getStop(){
    return start + time;
  }
  public int getMachine(){
    return machine;
  }
  public void setNextRow(Row nextRow){
    this.nextRow = nextRow;
  }
  public Row getNextRow(){
    return nextRow;
  }
}

class Data {

  public int m;
  public int n;
  public Row[][] tasks;
  public void read() {
    BufferedReader odczyt = new BufferedReader(new InputStreamReader(System.in));
    try{
      String[] x = odczyt.readLine().split(" ");
      n = Integer.parseInt(x[0]);
      m = Integer.parseInt(x[1]);
      tasks = new Row[n][3];
      for(int i = 0; i < n; i++) {
        x = odczyt.readLine().split(" ");
        for(int j = 0; j < m; j++){
          if(tasks[i][0] == null){
            tasks[i][0] = new Row(Integer.parseInt(x[j*2+1]), Integer.parseInt(x[j*2]), null);
            tasks[i][1] = tasks[i][0];
          }
          else{
            Row nr = new Row(Integer.parseInt(x[(j*2)+1]), Integer.parseInt(x[j*2]), tasks[i][1]);
            tasks[i][1].setNextRow(nr);
            tasks[i][1] = nr;
          }
        }
      }
      odczyt.close();
    }
    catch(IOException ioe){
      System.out.println("Problem z odczytem danych!");
      System.exit(1);
  }
  }

  public static Row[][] clonTasks(Row[][] x, int nn, int ll){
    Row[][] retasks = new Row[nn][ll];
    for(int i = 0; i<nn; i++)
      for(int j = 0; j < ll; j++)
        retasks[i][j] = x[i][j];
    return retasks;
  }
  public Row[][] getTasks(){
    return clonTasks(tasks, n,3);
  }

  public void print() {
    System.out.println("Tabela");
    System.out.println(m + " " + n);
    for(int i = 0; i < n; i++) {
      Row x = tasks[i][0];
      System.out.print(x.getMachine() + " " + x.getTime() + " ");
      do{
        x = x.getNextRow();
        System.out.print(x.getMachine() + " " + x.getTime() + " ");
      }while(x.getNextRow() != null);
      System.out.println();
    }
  }
}

class Solve{
  int apap = 0;
  Solve(){

  }
  private void work(Row act, int i, int pom, String[] resul, Row[][] tasks, int[] table){
    if(tasks[i][2]!=null){
      if(tasks[i][2].getStop()>table[act.getMachine()]){
        act.setStart(tasks[i][2].getStop());
      }
      else{
          act.setStart(table[act.getMachine()]);
      }
    }
    else
      act.setStart(table[act.getMachine()]);
    table[act.getMachine()] = act.getStop();
    if(resul[i] != "") resul[i] = resul[i] + " " + act.getStart();
    else resul[i] = act.getStart() + "";
    if(apap < act.getStart()+act.getTime()) apap = act.getStart()+act.getTime();
    tasks[i][2] = act;
  }

  public static String[] clonRes(String[] xx, int nn){
    String[] yy = new String[nn];
    for(int i = 0; i < nn; i++)
      yy[i] = xx[i];
    return yy;
  }


  private int szukaj(int granica, Data data, Row[][] tasks, int ostatni, int[]table){
    int actual = najdluzej(granica,data,tasks, table);
    if(actual == -1) return ostatni;
    if(tasks[actual][2]==null) return actual;
    if(tasks[actual][2]!=null && tasks[actual][2].getStop() <= table[tasks[actual][2].getNextRow().getMachine()])
      return actual;
    return szukaj(tasks[actual][2].getStop(),data,tasks,actual,table);
  }

  private int najdluzej(int granica, Data data, Row[][] tasks, int[] table){
    int maxi = -1, maxd = 0, ii=0, id=0;
    for(int i = 0; i<data.n; i++){
      if(tasks[i][2] == null && table[tasks[i][0].getMachine()] + tasks[i][0].getTime() <= granica){
        ii = i;
        id = tasks[i][0].pozCzasu();
      }
      if(tasks[i][2]!=null && tasks[i][2].getNextRow()!=null &&tasks[i][2].getStop() + tasks[i][2].getNextRow().getTime() <= granica){
        ii = i;
        id = tasks[i][2].getNextRow().pozCzasu();
      }
      if(maxd < id){
        maxd = id;
        maxi = ii;
      }
    }
    return maxi;
  }

  public String[] simple(Data data, int[] kolej, int ko, int nrko)  throws IOException{
    String[] resul;
    Row[][] tasks;
    int[] table;
    tasks = data.getTasks();
    int n = data.n;
    int m = data.m;
    resul = new String[n];
    for(int i = 0; i < n; i++) resul[i] = "";
    table = new int[m];
    for(int ii = 0; ii < n*m; ii++){
      int ost=0;
      for(int i = 0; i <n; i++){
        if(tasks[i][2]!=null && tasks[i][2].getNextRow()!=null){
          ost = i;
          break;
        }
      }
      int i;
      if(nrko == ii){
        i = ko;
      }else if(kolej[ii] != -1){
        i = kolej[ii];
      }else i = szukaj(199999999, data, tasks, ost, table);
      if(tasks[i][2] == null) {
        work(tasks[i][0], i, 0, resul, tasks, table);
      }
      else{
        work(tasks[i][2].getNextRow(), i, 0, resul, tasks, table);
      }
    }
    return clonRes(resul, n);
  }

  public static void print(String[] resul, Data data) throws IOException{
    BufferedWriter zapis = new BufferedWriter(new OutputStreamWriter(System.out));
    for(int i = 0; i < data.n; i++){
      zapis.write(resul[i]+ "\n");
    }
    zapis.flush();
  }
}




class JobShop {
  public static void main(String args[]) throws IOException{
    long start_time = System.nanoTime();
    Data data = new Data();
    data.read();
    int[] kolej = new int[data.n*data.m];
    int[] licz = new int[data.n];
    String[] najs = new String[data.n];
    int naji = 999999999;
    int najip = 0;
    for(int i = 0; i < data.n*data.m; i++) kolej[i] = -1;
    boolean nawrot = false;
    int maxtime = 179;
    if( (data.n == 20 && data.m == 20) ||  (data.n == 30 && data.m == 20) ||
      (data.n == 50 && data.m == 10) ||(data.n == 50 && data.m == 20) ||
      (data.n == 100 && data.m == 20) ) maxtime = 299;

    //

    //while((System.nanoTime() - start_time)/1000000000 < maxtime){
    int[] kolej_poprz = new int[data.n*data.m];
    boolean zmiana = true;
    while(zmiana && (System.nanoTime() - start_time)/1000000000 < maxtime){
      for(int j = 0; j < data.n*data.m; j++){
        kolej_poprz[j] = kolej[j];
      }
      for(int i = 0; i < data.n; i++)licz[i] = 0;
      for(int i = 0; i < data.n*data.m; i++){
        int xx = -1;
        naji = 999999999;
        for(int l = 0; l < data.n; l++){
          if(licz[l] == data.m) continue;
          Data data_copy = data;
          Solve sol = new Solve();
          String[] ss = sol.simple(data_copy, kolej, l, i);

          if(naji > sol.apap){
            for(int j = 0; j < data.n; j++){
              naji = sol.apap;
              najs[j] = ss[j];
            }
            xx = l;
            licz[l]++;
          }
        }
        kolej[i] = xx;
        System.out.print(xx+ " ");
      }
      System.out.println();
      zmiana = false;
      for(int j = 0; j < data.n; j++){
        if(kolej_poprz[j] != kolej[j]){
          zmiana = true;
          break;
        }
      }
    }




/*
    nawrot = true;
    int xyz = data.m*data.m*5;
    while((System.nanoTime() - start_time)/1000000000 < maxtime && xyz >= 0){
      xyz--;
      for(int i = 0; i < data.n; i++)licz[i] = 0;
     for(int i = 0; i < data.n*data.m; i++){
        kolej[i] = -1;
      }
      Random rand = new Random();
      for(int i = 0; i < data.m; i++){
        kolej[i] = rand.nextInt(data.n);
        licz[kolej[i]]++;
      }


      Data data_copy = data;
      Solve sol = new Solve();
      String[] ss = sol.simple(data_copy, kolej, -1, -1);

      if(najip > sol.apap){
        System.out.println(sol.apap + " " + najip);
        for(int j = 0; j < data.n; j++){
          najs[j] = ss[j];
        }
        najip = sol.apap;
        break;
      }
    }
    }
*/



    Solve.print(najs, data);
  }
}
