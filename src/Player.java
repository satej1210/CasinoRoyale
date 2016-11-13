/**
 * Created by satejmhatre on 10/26/16.
 */
import CR.*;
import DDS.DataWriter;
import DDS.HANDLE_NIL;

import java.util.Scanner;


public class Player  {
    private bjPlayer player = null;
    private Player(){
        player = new bjPlayer();
        player.uuid = UUIDGen.generate_UUID();
        player.credits = 100;
        player.action = bjp_action.joining;
        player.seqno = 0;
    }
    public static void Publish(String message, Player p){
        Runnable b = ()->{
            DDSEntityManager mgr = new DDSEntityManager();
            String partitionName = "Player";
            // create Domain Participant
            mgr.createParticipant(partitionName);
            // create Type
            bjPlayerTypeSupport msgTS = new bjPlayerTypeSupport();
            mgr.registerType(msgTS);
            // create Topic
            mgr.createTopic("Player");
            // create Publisher
            mgr.createPublisher();
            // create DataWriter
            mgr.createWriter();
            // Publish Events
            DataWriter dwriter = mgr.getWriter();
            bjPlayerDataWriter playerWriter = bjPlayerDataWriterHelper.narrow(dwriter);
//            bjPlayer msgInstance = new bjPlayer();
            System.out.println("=== [Publisher] writing a message containing :");
            System.out.println("    uuid  : " + p.player.uuid);
            System.out.println("    seqno : \"" + p.player.seqno + "\"");
            playerWriter.register_instance(p.player);
            int status = playerWriter.write(p.player, HANDLE_NIL.value);
            ErrorHandler.checkStatus(status, "MsgDataWriter.write");
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // clean up
            mgr.getPublisher().delete_datawriter(playerWriter);
            mgr.deletePublisher();
            mgr.deleteTopic();
            mgr.deleteParticipant();
        };
        Thread w = new Thread(b);
        w.start();


    }
    public static void main(String args[]){
        Player p = new Player();

        System.out.println(MAX_PLAYERS.value);
        Scanner reader = new Scanner(System.in);
//        System.out.println("Enter a number: ");
//        int n = reader.nextInt();
//        System.out.println("number: " + n);
        Publish("", p);

    }
}
