
/**
 * Created by satejmhatre on 10/26/16.
 */

import CR.*;
import DDS.*;

import java.util.ArrayList;
import java.util.Scanner;

// this is the main player class
// helps add functionality to bjPlayer
public class Player {
    private static boolean gameRunning = true;
    private bjPlayer player = null;
    private ArrayList<CR.card> playerCards;
    private Thread Sub, Pub;
    private int playerMode = 0; //1 : Mr. Conservative, 2 : Mr. I believe in Luck, 3 : Mr. Card Counter
    Player() {
        player = new bjPlayer();
        player.uuid = UUIDGen.generate_UUID();
        player.dealer_id = 0;
        player.wager = 0;
        player.credits = 100;
        player.action = bjp_action.joining;
        player.seqno = 0;
        playerCards = new ArrayList<>();
    }
    public static void main(String args[]) {
        Player p = new Player();
        Scanner reader = new Scanner(System.in);
        System.out.println("Choose Mode:\n1. Mr. Conservative\n2. Mr. I believe in Luck\n3. Mr. Card Counter\n: 1");
        int opt = 1;
        p.playerMode = opt;
        p.Publish(bjp_action.joining);
        p.SubscribeDealer();
    }
    public void PlayerPrint(String x) {
        System.out.println("[Player " + this.player.uuid + " !! SeqNo: " + this.player.seqno + " !! Credits = " + this.player.credits + "] " + x);
    }

    // Dealer collects info from player publisher
    public void DealerCollecting(bjDealer d) {
        PlayerPrint("Message received from Dealer with id " + d.uuid + " :");
        Scanner a = new Scanner(System.in);
        PlayerPrint("Wagering");
        //int x = a.nextInt();
        int x = 10;
        if (playerMode == 1) {
            x = 1;
        } else if (playerMode == 2) {
            //Luck Stuff
        } else {
            //CardCounter Stuff
        }
        if (x < this.player.credits) {
            this.player.credits = this.player.credits - x;
            this.player.wager = x;
        } else {
            PlayerPrint("You don't have enough credits!!!");
        }
        this.Publish(bjp_action.wagering);
    }

    // checks if the sum is less than or equal to or greater than 21
    // if sum > 21, check is ace exists
    // if yes, count ace as 1
    // else, player busted
    // if sum < 21, player stays and dealer plays
    // if sum = 21, player stays and dealer plays
    public String CheckSum(int sum, int aceExists) {
        String s = "L21";
        if (sum > 21) {
            while (aceExists != 0) {
                sum -= 10;
                aceExists--;
                if (sum == 21) {
                    s = ("Sum = 21!");
                    break;
                }
            }
            if (sum > 21) {
                s = ("Sum = " + sum + "\nBUSTED!");
                this.Publish(bjp_action.exiting);
            }
        } else if (sum == 21) {
            s = ("Sum = 21!");
        }
        return s;
    }

    // this method asks the player to either hit or stay
    // on player choice, hit or stay and print sum
    public void PlayerModePlay(int sum) {
        if (playerMode == 1) {
            Scanner a = new Scanner(System.in);
            PlayerPrint("Sum = " + sum + "\nHit(1) or Stay(2)?:");
            int x;
            if (sum < 17) {
                x = 1;
            } else {
                x = 2;
            }
            if (x == 1) {
                this.Publish(bjp_action.requesting_a_card);
            } else {
                PlayerPrint("Player is staying. Sum = " + sum);
                this.Publish(bjp_action.none);
            }
        }
    }
    /**
     * The PlayCards method gets the cards from the dealer and adds that card to the player's hand of cards. It then calls
     * the CheckSum function. Then the player is asked to Hit or Stay. The function then Publishes with the appropriate response.
     *
     * @param d The dealer of the player
     */
    public void PlayCards(bjDealer d) {
        int sum = 0;
        card c = new card();
        c.base_value = d.cards[0].base_value;
        c.suite = d.cards[0].suite;
        c.visible = d.cards[0].visible;
        playerCards.add(c);
        CardFunctions.PrintCards(playerCards);
        int aceExists = 0;
        for (int i = 0; i < playerCards.size(); ++i) {
            if (playerCards.get(i).base_value == 'A') {
                aceExists++;
            }
            sum += CardFunctions.GetValue(playerCards.get(i).base_value);
        }
        PlayerPrint(this.CheckSum(sum, aceExists));
        PlayerModePlay(sum);
    }

    // check if player wins, print player wins and add credits to player
    // check if dealer wins and print dealer wins
    // check if no one wins and print no one wins
    public void PlayerWin(bjDealer d) {
        player_status m = null;
        for (player_status ps : d.players) {
            if (ps.uuid == this.player.uuid) {
                m = ps;
            }
        }
        if (m != null) {
            if (m.wager != 0) {
                PlayerPrint("Player Wins!!!");
                float pay = (float) 3 / 2 * (float) m.wager;
                this.player.credits += pay;
                PlayerPrint("Credits = " + this.player.credits);
            } else if (m.wager < 0) {
                PlayerPrint("Dealer Wins :(");
            } else {
                PlayerPrint("No one wins");
            }
        }
    }

    // this is the dealers subscriber which will communicate with the players publisher

    /**
     * The SubscribeDealer function is the main part of the program
     */
    public void SubscribeDealer() {
        Runnable b = () -> {
            DDSEntityManager mgr = new DDSEntityManager();              // create a new DDS instance
            mgr.createParticipant("CR");                                // create Domain Participant
            bjDealerTypeSupport msgTS = new bjDealerTypeSupport();      // create Type for Dealer
            mgr.registerType(msgTS);                                    // Register Dealer
            mgr.createTopic("Dealer");                                  // create Topic
            mgr.createSubscriber();                                     // create Subscriber
            mgr.createReader();                                         // create DataReader
            DataReader dreader = mgr.getReader();                       // Read Events
            bjDealerDataReader dealerReader = bjDealerDataReaderHelper.narrow(dreader);
            bjDealerSeqHolder msgSeq = new bjDealerSeqHolder();
            SampleInfoSeqHolder infoSeq = new SampleInfoSeqHolder();
            PlayerPrint("Player is now Subscribing to Dealer...");
            boolean terminate = false;
            while (!terminate) {                                        // We don't want the example to run indefinitely
                dealerReader.take(msgSeq,                               // This reads the data that is Published by the dealer
                        infoSeq,
                        LENGTH_UNLIMITED.value,
                        ANY_SAMPLE_STATE.value, ANY_VIEW_STATE.value,
                        ANY_INSTANCE_STATE.value);
                for (int i = 0; i < msgSeq.value.length; i++) {         // If multiple dealers communicate with the player,
                    // then this for loop iterates over their messages
                    bjDealer d = msgSeq.value[i];
                    if (d.getClass() == bjDealer.class) {               // Only read data if it's a Dealer
                        if (this.player.dealer_id == 0) {               // To set dealer_id for Player
                            if (d.action == bjd_action.collecting) {
                                PlayerPrint("Dealer has accepted and is collecting!");
                                this.player.dealer_id = d.uuid;
                                this.DealerCollecting(d);
                                this.player.seqno++;
                                continue;
                            }
                        }
                        if (d.target_uuid == this.player.uuid) {        // This is when the Dealer wants to communicate with this player
                            if (d.action == bjd_action.waiting) {       // If in case the Dealer did not detect the player
                                PlayerPrint("Dealer has already Entered!");
                            }
                            if (d.action == bjd_action.dealing) {       // Things to do when the Dealer is dealing
                                PlayerPrint("Cards Recieved");
                                this.PlayCards(d);
                            }
                            if (d.action == bjd_action.paying) {        // Things to do when the Dealer is paying player
                                this.PlayerWin(d);
                                terminate = true;
                                gameRunning = false;
                            }
                        }
                        this.player.seqno++;
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    // nothing to do
                }
            }
            dealerReader.return_loan(msgSeq, infoSeq);
            // clean up
            // delete DataReader
            mgr.getSubscriber().delete_datareader(dealerReader);
            // delete Subscriber
            mgr.deleteSubscriber();
            // delete Topic
            mgr.deleteTopic();
            // delete Participant
            mgr.deleteParticipant();
            PlayerPrint("Subscriber Closing.");
//            main(new String[2]);
        };
        Sub = new Thread(b);
        Sub.start();
    }
    // this creates the players
    public void Publish(bjp_action action) {
        DDSEntityManager mgr = new DDSEntityManager();
        // create Domain Participant
        mgr.createParticipant("CR");
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
        playerWriter.register_instance(this.player);
        this.player.action = action;
        if (action == bjp_action.joining) {
//            SubscribeDealer();
        } else if (action == bjp_action.wagering) {
            PlayerPrint("Player is wagering");
        }
        int status = playerWriter.write(this.player, HANDLE_NIL.value);
        ErrorHandler.checkStatus(status, "MsgDataWriter.write");
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ie) {
            // nothing to do
        }
        this.player.seqno++;
        // clean up
        // delete DataWriter
        mgr.getPublisher().delete_datawriter(playerWriter);
        // delete Publisher
        mgr.deletePublisher();
        // delete Topic
        mgr.deleteTopic();
        // delete Domain Participant
        mgr.deleteParticipant();
    }
}