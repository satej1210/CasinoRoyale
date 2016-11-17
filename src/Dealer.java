/**
 * Created by Satej on 11/14/2016.
 */

import CR.*;
import DDS.*;

import java.util.ArrayList;
import java.util.Scanner;

public class Dealer {
    public final static int DECKS = 6;
    private bjDealer dealer;
    private ArrayList<CR.card> cards;

    Dealer() {
        dealer = new bjDealer();
        dealer.uuid = UUIDGen.generate_UUID();
        dealer.cards = new card[21];
        dealer.action = bjd_action.waiting;
        dealer.seqno = 0;
        cards = new ArrayList<>();
        CardFunctions.GenerateDeck(cards);
    }

    Dealer(bjDealer d) {
        dealer = d;
    }

    public static void PublishInit(String m, Dealer p) {
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
            Thread.sleep(1000);
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
        PublishInit("Dealer_init", d);
        d.dealer.action = bjd_action.shuffling;

//        CardFunctions.PrintDeck(d.cards);
//
//        CardFunctions.PrintDeck(d.cards);

        System.out.println(d.dealer.cards.length);
        card c;
        CardFunctions.GenerateDeck(d.cards);

        d.cards = CardFunctions.ShuffleCards(d.cards);
        d.AskBets();
//        d.dealer.cards = cd.c.clone();
//        System.out.println(d.dealer.cards.length);
//        System.out.println(cd.card);
        System.out.println(MAX_PLAYERS.value);
        Scanner reader = new Scanner(System.in);

        System.out.println("Enter a number: ");
        int n = reader.nextInt();
        System.out.println("number: " + n);
//        Publish("Dealer", d);

    }

    public void Subscribe() {
        DDSEntityManager mgr = new DDSEntityManager();
        String partitionName = "Player";

        // create Domain Participant
        mgr.createParticipant(partitionName);

        // create Type
        bjPlayerTypeSupport msgTS = new bjPlayerTypeSupport();
        mgr.registerType(msgTS);

        // create Topic
        mgr.createTopic("Player");

        // create Subscriber
        mgr.createSubscriber();

        // create DataReader
        mgr.createReader();

        // Read Events

        DataReader dreader = mgr.getReader();
        bjPlayerDataReader HelloWorldReader = bjPlayerDataReaderHelper.narrow(dreader);

        bjPlayerSeqHolder msgSeq = new bjPlayerSeqHolder();
        SampleInfoSeqHolder infoSeq = new SampleInfoSeqHolder();

        System.out.println("=== [Subscriber] Ready ...");
        boolean terminate = false;
        int count = 0;
        while (!terminate) { // We dont want the example to run indefinitely
            HelloWorldReader.take(msgSeq, infoSeq, LENGTH_UNLIMITED.value,
                    ANY_SAMPLE_STATE.value, ANY_VIEW_STATE.value,
                    ANY_INSTANCE_STATE.value);
            for (int i = 0; i < msgSeq.value.length; i++) {
                if (msgSeq.value[i].getClass() == bjPlayer.class && msgSeq.value[i].dealer_id == this.dealer.uuid) {//.message.equals("Hello World")) {
                    System.out.println("=== [DealerSubscriber] message received :");
                    player_status p = null;
                    for (player_status ps : this.dealer.players) {
                        if (ps.uuid == msgSeq.value[i].uuid) {
                            ps.wager = msgSeq.value[i].wager;
                            System.out.println(" === Player with uuid " + ps.uuid + " set bet of " + ps.wager);
                            break;
                        }
                    }
                }
                terminate = true;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException ie) {
                // nothing to do
            }
            ++count;

        }
        HelloWorldReader.return_loan(msgSeq, infoSeq);

        // clean up
        mgr.getSubscriber().delete_datareader(HelloWorldReader);
        mgr.deleteSubscriber();
        mgr.deleteTopic();
        mgr.deleteParticipant();

    }

    public void AskBets() {
        DDSEntityManager mgr = new DDSEntityManager();
        String partitionName = "Dealer";

        // create Domain Participant
        mgr.createParticipant(partitionName);

        // create Type
        bjDealerTypeSupport msgTS = new bjDealerTypeSupport();
        mgr.registerType(msgTS);

        // create Topic
        mgr.createTopic("Dealer");

        // create Publisher
        mgr.createPublisher();

        // create DataWriter
        mgr.createWriter();

        // Publish Events

        DataWriter dwriter = mgr.getWriter();
        bjDealerDataWriter HelloWorldWriter = bjDealerDataWriterHelper.narrow(dwriter);
        bjDealer msgInstance = new bjDealer();
        msgInstance.uuid = this.dealer.uuid;
        msgInstance.seqno = this.dealer.seqno;
        msgInstance.action = bjd_action.collecting;
        msgInstance.active_players = this.dealer.active_players;

        msgInstance.target_uuid = this.dealer.target_uuid;
        System.out.println("==1de= [DealerPublisher] is asking for bets :");
        HelloWorldWriter.register_instance(msgInstance);
        int status = HelloWorldWriter.write(msgInstance, HANDLE_NIL.value);
        ErrorHandler.checkStatus(status, "MsgDataWriter.write");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // clean up
        mgr.getPublisher().delete_datawriter(HelloWorldWriter);
        mgr.deletePublisher();
        mgr.deleteTopic();
        mgr.deleteParticipant();
    }

    @Override
    public String toString() {
        return "\tUUID : " + this.dealer.uuid + "\n\taction: " + this.dealer.action;

    }
}
