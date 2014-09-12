package br.com.uol.ps.pagseguroexemplo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.math.BigDecimal;

import br.com.uol.ps.library.PagSeguro;
import br.com.uol.ps.library.PagSeguroRequest;
import br.com.uol.ps.library.PagSeguroResponse;


public class MainActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PagSeguro.onActivityResult(this, requestCode, resultCode, data);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private Button fiftyCent;
        private Button oneReal;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            oneReal = (Button) rootView.findViewById(R.id.one_real);
            oneReal.setOnClickListener(payWithPagSeguro());

            return rootView;
        }

        private View.OnClickListener payWithPagSeguro() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PagSeguro.pay(new PagSeguroRequest().withAmount(new BigDecimal(1.00))
                                    .withVendorEmail("suporte@lojamodelo.com.br")
                                    .withBuyerEmail("comprador@mail.com.br")
                                    .withBuyerCellphoneNumber("5511992190364")
                                    .withReferenceCode("123"),
                            getActivity(),
                            R.id.container,
                            new PagSeguro.PagSeguroListener() {
                                @Override
                                public void onSuccess(PagSeguroResponse response, Context context) {
                                    Toast.makeText(context, "Lib PS retornou pagamento aprovado!", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onFailure(PagSeguroResponse response, Context context) {
                                    Toast.makeText(context, "Lib PS retornou FALHA no pagamento!", Toast.LENGTH_LONG).show();
                                }
                            });

                }
            };
        }

    }
}