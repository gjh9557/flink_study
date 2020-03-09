public class LinkTest {
    public static void main(String[] args) {
        Linked node1=new Linked(1);
        Linked node2=new Linked(2);
        Linked node3=new Linked(3);
        Linked node4=new Linked(4);
        Linked node5=new Linked(5);
        node1.next=node3;
        node3.next=node5;
        node2.next=node4;
        Linked head=null;
       Linked now=merge(node1, node2,head);

       sout(now);

    }
    public static void sout(Linked show){
        if(show!=null){
            System.out.println(show.val);
            sout(show.next);
        }
    }
    public static Linked merge(Linked node1,Linked node2,Linked head){
        if(node1==null){
            return  node2;
        }
        if(node2==null){
            return  node1;
        }

        if(node1.val>node2.val){
            head=node2;
            head.next=merge(node1,node2.next,head);
        }
        if (node2.val>=node1.val){
            head=node1;
            head.next=merge(node1.next,node2,head);
        }

    return head;}

}
