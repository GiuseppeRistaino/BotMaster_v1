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
            public void onCoinsReceived(Wallet w, Transaction tx, Coin prevBalance, Coin newBalance) {
                Coin value = tx.getValueSentToMe(w);
                //System.out.println("Received tx for " + value.toFriendlyString() + ": " + tx);
                Log.d("App", "Received tx for " + value.toFriendlyString() + ": " + tx);
                Toast.makeText(WalletActivity.this, "Received tx for " + value.toFriendlyString() + ": " + tx, Toast.LENGTH_SHORT).show();

                //Da cambiare!!!!!!!!!!!!!!!!!!!! In Android non funziona....
                Futures.addCallback(tx.getConfidence().getDepthFuture(1), new FutureCallback<TransactionConfidence>() {
                    @Override
                    public void onSuccess(TransactionConfidence result) {
                        //System.out.println("Transazione ricevuta con successo");
                        //System.out.println("Il Master ha: " +w.getBalance().toFriendlyString());
                        Log.d("App", "Transazione ricevuta con successo");
                        appState.saveWallet();
                        textviewWallet.setText(appState.wallet.toString());
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        // This kind of future can't fail, just rethrow in case something weird happens.
                        //System.out.println("Transazione non ricevuta");
                        Log.d("App", "Transazione non ricevuta");
                    }
                });
            }
        });
    }
}
