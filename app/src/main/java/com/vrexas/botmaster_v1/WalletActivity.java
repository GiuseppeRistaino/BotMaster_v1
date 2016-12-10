package com.vrexas.botmaster_v1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;

public class WalletActivity extends AppCompatActivity {

    ApplicationState appState;

    TextView textviewWallet;
    TextView textViewAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        appState = (ApplicationState) getApplication();

        textviewWallet = (TextView) this.findViewById(R.id.textView_wallet);
        textViewAddress = (TextView) this.findViewById(R.id.textView_address);

        textviewWallet.setText(appState.wallet.toString());

        textViewAddress.setText(appState.wallet.currentReceiveKey().toAddress(appState.params).toString());

        appState.wallet.addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet w, final Transaction tx, Coin prevBalance, Coin newBalance) {
                final Coin value = tx.getValueSentToMe(w);
                //System.out.println("Received tx for " + value.toFriendlyString() + ": " + tx);
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(WalletActivity.this, "Received tx for " + value.toFriendlyString() + ": " + tx, Toast.LENGTH_SHORT).show();
                        Log.d("App", "Received tx for " + value.toFriendlyString() + ": " + tx);
                    }
                });

                Futures.addCallback(tx.getConfidence().getDepthFuture(1), new FutureCallback<TransactionConfidence>() {
                    @Override
                    public void onSuccess(TransactionConfidence result) {

                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(WalletActivity.this, "Received tx for " + value.toFriendlyString() + ": " + tx, Toast.LENGTH_SHORT).show();
                                Log.d("App", "Transazione ricevuta con successo");
                                appState.saveWallet();
                                textviewWallet.setText(appState.wallet.toString());
                            }
                        });
                    }
                    @Override
                    public void onFailure(Throwable t) {
                        //System.out.println("Transazione non ricevuta");
                        Log.d("App", "Transazione non ricevuta");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(WalletActivity.this, "Transazione non ricevuta", Toast.LENGTH_SHORT).show();
                                Log.d("App", "Transazione non ricevuta");
                            }
                        });
                    }
                });
            }
        });
    }
}
