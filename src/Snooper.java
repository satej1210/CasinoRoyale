/**
 * Created by satejmhatre on 10/26/16.
 */

import CR.*;
import DDS.*;

public class Snooper {

    Snooper() {

    }


    public static void main(String... args) {
        Snooper s = new Snooper();
        s.SubscribeToPlayer();
        s.SubscribeToDealer();
    }


    public void SubscribeToPlayer() {
        DDSEntityManager mgr = new DDSEntityManager();
        String partitionName = "CR";

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

        DataReader dealerReader = mgr.getReader();
        bjPlayerDataReader bjPlayerReader = bjPlayerDataReaderHelper.narrow(dealerReader);

        bjPlayerSeqHolder bjPlayerSeq = new bjPlayerSeqHolder();
        SampleInfoSeqHolder infoSeq = new SampleInfoSeqHolder();

        System.out.println("From GM  === [Subscriber] Ready for Player ...");
        Runnable backGroundRunnable = () -> {

            boolean terminate = false;
            int count = 0;
            while (!terminate) { // We dont want the example to run indefinitely
                bjPlayerReader.take(bjPlayerSeq, infoSeq, LENGTH_UNLIMITED.value,
                        ANY_SAMPLE_STATE.value, ANY_VIEW_STATE.value,
                        ANY_INSTANCE_STATE.value);
                for (int i = 0; i < bjPlayerSeq.value.length; i++) {
                    if (bjPlayerSeq.value[i].getClass() == bjPlayer.class) {
                        bjPlayer p = new bjPlayer();
                        if (p.action == bjp_action.joining) {
                            SnooperPrint("Player Joining");
                        }
                        if (p.action == bjp_action.wagering) {
                            SnooperPrint("Player Wagering");
                        }
                        if (p.action == bjp_action.requesting_a_card) {
                            SnooperPrint("Player Requesting");
                        }
                        if (p.action == bjp_action.exiting) {
                            SnooperPrint("Player Exiting");
                        }
                        if (p.action == bjp_action.none) {
                            SnooperPrint("Player Staying");
                        }


                    }

                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException ie) {
                    // nothing to do
                }
                ++count;

            }
            bjPlayerReader.return_loan(bjPlayerSeq, infoSeq);
            System.out.println("Stopping Thread");
            // clean up
            mgr.getSubscriber().delete_datareader(bjPlayerReader);
            mgr.deleteSubscriber();
            mgr.deleteTopic();
            mgr.deleteParticipant();

        };
        Thread sampleThread = new Thread(backGroundRunnable);

        sampleThread.start();
    }

    public void SnooperPrint(String x) {
        System.out.println("[Snooper] " + x);
    }

    public void SubscribeToDealer() {
        DDSEntityManager mgr = new DDSEntityManager();
        String partitionName = "CR";

        // create Domain Participant
        mgr.createParticipant(partitionName);

        // create Type
        bjDealerTypeSupport msgTS = new bjDealerTypeSupport();
        mgr.registerType(msgTS);

        // create Topic
        mgr.createTopic("Dealer");

        // create Subscriber
        mgr.createSubscriber();

        // create DataReader
        mgr.createReader();

        // Read Events

        DataReader dealerReader = mgr.getReader();
        bjDealerDataReader bjDealerReader = bjDealerDataReaderHelper.narrow(dealerReader);

        bjDealerSeqHolder bjDealerSeq = new bjDealerSeqHolder();
        SampleInfoSeqHolder infoSeq = new SampleInfoSeqHolder();

        System.out.println("From GM === [Subscriber] Ready ...");
        Runnable backGroundRunnable = () -> {

            boolean terminate = false;
            int count = 0;
            while (!terminate) { // We dont want the example to run indefinitely
                bjDealerReader.take(bjDealerSeq, infoSeq, LENGTH_UNLIMITED.value,
                        ANY_SAMPLE_STATE.value, ANY_VIEW_STATE.value,
                        ANY_INSTANCE_STATE.value);
                for (int i = 0; i < bjDealerSeq.value.length; i++) {
                    bjDealer d = bjDealerSeq.value[i];
                    if (d.getClass() == bjDealer.class) {

                        if (d.action == bjd_action.waiting) {
                            SnooperPrint("Dealer has already Entered!");
                        }
                        if (d.action == bjd_action.collecting) {
                            SnooperPrint("Dealer collecting!");
                            continue;
                        }
                        if (d.action == bjd_action.dealing) {
                            SnooperPrint("Cards Dealed");

                        }
                        if (d.action == bjd_action.paying) {
                            SnooperPrint("Player Wins!!!");
                        }


                    }

                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ie) {
                    // nothing to do
                }
                ++count;

            }
            bjDealerReader.return_loan(bjDealerSeq, infoSeq);

            // clean up
            mgr.getSubscriber().delete_datareader(bjDealerReader);
            mgr.deleteSubscriber();
            mgr.deleteTopic();
            mgr.deleteParticipant();
            System.out.println("Stopping Thread");
        };
        Thread sampleThread = new Thread(backGroundRunnable);
        sampleThread.start();
    }

}
