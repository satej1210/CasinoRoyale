/**
 * Created by Satej on 11/14/2016.
 */

import CR.*;
import DDS.DataWriter;
import DDS.HANDLE_NIL;

import java.util.ArrayList;
import java.util.Scanner;

public class Dealer {
    public final static int DECKS = 6;
    private bjDealer dealer;
    private ArrayList<CR.card> cards;

    private Dealer() {
        dealer = new bjDealer();
        dealer.uuid = UUIDGen.generate_UUID();
        dealer.cards = new card[21];
        dealer.action = bjd_action.waiting;
        dealer.seqno = 0;
        cards = new ArrayList<>();
        CardFunctions.GenerateDeck(cards);
    }

    public static void Publish(String m, Dealer p) {
        DDSEntityManager mgr = new DDSEntityManager();
        String partitionName = "Dealer_init";

        // create Domain Participant
        mgr.createParticipant(partitionName);

        // create Type
        bjDealerTypeSupport msgTS = new bjDealerTypeSupport();
        mgr.registerType(msgTS);

        // create Topic
        mgr.createTopic("Dealer_init");

        // create Publisher
        mgr.createPublisher();

        // create DataWriter
        mgr.createWriter();

        // Publish Events

        DataWriter dwriter = mgr.getWriter();
        bjDealerDataWriter HelloWorldWriter = bjDealerDataWriterHelper.narrow(dwriter);
        bjDealer msgInstance = new bjDealer();
        msgInstance.uuid = p.dealer.uuid;
        msgInstance.seqno = p.dealer.seqno;
        msgInstance.action = p.dealer.action;
        msgInstance.active_players = p.dealer.active_players;

        msgInstance.target_uuid = p.dealer.target_uuid;
        System.out.println("==1de= [Publisher] writing a message containing :");
        System.out.println("    userID  : " + msgInstance.uuid);
        System.out.println("    Message : \"" + msgInstance.seqno + "\"");
        HelloWorldWriter.register_instance(msgInstance);
        int status = HelloWorldWriter.write(msgInstance, HANDLE_NIL.value);
        ErrorHandler.checkStatus(status, "MsgDataWriter.write");
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // clean up
        mgr.getPublisher().delete_datawriter(HelloWorldWriter);
        mgr.deletePublisher();
        mgr.deleteTopic();
        mgr.deleteParticipant();


    }

    public static void main(String args[]){
        Dealer d = new Dealer();
        Publish("Dealer_init", d);
        d.dealer.action = bjd_action.shuffling;

        CardFunctions.ShuffleCards(d.cards);
        CardFunctions.PrintDeck(d.cards);
        System.out.println(d.dealer.cards.length);
        CardAndDeck cd;
        cd = CardFunctions.PickCard(d.cards);
        d.dealer.cards = cd.c.clone();
        System.out.println(d.dealer.cards.length);
        System.out.println(cd.card);
        System.out.println(MAX_PLAYERS.value);
        Scanner reader = new Scanner(System.in);

//        System.out.println("Enter a number: ");
//        int n = reader.nextInt();
//        System.out.println("number: " + n);
        //Publish("Player", p);

    }
}
