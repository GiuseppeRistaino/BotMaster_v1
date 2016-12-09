package com.vrexas.botmaster_v1;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.bitcoinj.core.listeners.DownloadProgressTracker;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ApplicationState appState;

    ProgressBar progressBar;
    TextView textView;

    DownloadProgressTracker downloadTracker;
    int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appState = (ApplicationState) getApplication();


        progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
        textView = (TextView) this.findViewById(R.id.textView);

        Log.d("App", "MainActivity");


        new DownloadBlockchain().execute();

    }

    private class DownloadBlockchain extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... par) {
            //PeerGroup peerGroup = new PeerGroup(appState.params, appState.chain);
            //peerGroup.addPeerDiscovery(new DnsDiscovery(appState.params));
            //peerGroup.addWallet(appState.wallet);
            Log.d("App", "Scarico la blockchain");

            appState.peerGroup.start();
            appState.peerGroup.startBlockChainDownload(downloadTracker = new DownloadProgressTracker() {

                @Override
                protected void progress(double pct, int blocksSoFar, Date date) {
                    publishProgress((int) pct);
                }

                @Override
                public void doneDownload() {
                    publishProgress(100);
                }

            });
            //appState.downloadBlockchainFromPeers(downloadTracker);
            try {
                downloadTracker.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e("App", "Errore durante il download della blockchain");
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
            textView.setText(values[0] + " %");
        }

        @Override
        protected void onPostExecute(String s) {

            /*
                   RICHIAMARE LA PROSSIMA ACTIVITY (LISTA DEI BOT) QUI E PASSARLE L'OGGETTO WalletAppKit
                   Ma non so come fare...forse Content Provider???
             */
            appState.saveWallet();

            Intent intent = new Intent(MainActivity.this, WalletActivity.class);
            startActivity(intent);
        }


    }



}
