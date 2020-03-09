//import scala.collection.immutable.List;

import java.util.LinkedList;
import java.util.List;

public class TestList {
    public static void main(String[] args) {
        TreeNode node1=new TreeNode(1);
        TreeNode node2=new TreeNode(2);
        TreeNode node3=new TreeNode(3);
        TreeNode node4=new TreeNode(4);
        TreeNode node5=new TreeNode(5);
        node1.NodeLedf=node2;
        node2.NodeLedf=node4;
        node1.NodeRight=node3;
        node3.NodeRight=node5;
        List<TreeNode> list=inorderroot(node1);
        for (TreeNode a:list
             ) {
            System.out.println(a);
        }

    }
   public static List<TreeNode> inorderroot(TreeNode node){
        List<TreeNode> list=new LinkedList<>();
        if(node!=null){
            inorder(node,list);
            return list;
        }else{return null;}
    }
    static void inorder(TreeNode node, List<TreeNode> list){

//        list.add(node);先序遍历
        if (node.NodeLedf!=null){
            inorder(node.NodeLedf,list);
        }
//        list.add(node); 中序遍历
        if(node.NodeRight!=null){
            inorder(node.NodeRight,list);
        }
//        list.add(node); 后序遍历


    }
}
