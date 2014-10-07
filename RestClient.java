package br.com.uol.ps.core.api;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import br.com.uol.ps.core.api.client.GzipGsonConverter;
import br.com.uol.ps.core.api.client.RestDFWallet;
import br.com.uol.ps.core.api.client.WalletRequestCallback;
import br.com.uol.ps.core.api.vo.AddCreditCardResponseVO;
import br.com.uol.ps.core.api.vo.BaseDFResponseVO;
import br.com.uol.ps.core.api.vo.BaseResponseVO;
import br.com.uol.ps.core.api.vo.CardBrandResponseVO;
import br.com.uol.ps.core.api.vo.CartResponseVO;
import br.com.uol.ps.core.api.vo.CityVO;
import br.com.uol.ps.core.api.vo.CreditCardResponseVO;
import br.com.uol.ps.core.api.vo.DFStartSessionResponseVO;
import br.com.uol.ps.core.api.vo.HistoryWalletTransactionResponseVO;
import br.com.uol.ps.core.api.vo.ModulusExponentVO;
import br.com.uol.ps.core.api.vo.PlaceVO;
import br.com.uol.ps.core.api.vo.PostalCodeResponseVO;
import br.com.uol.ps.core.api.vo.ResponseInterface;
import br.com.uol.ps.core.api.vo.SetupResponseVO;
import br.com.uol.ps.core.api.vo.SetupVO;
import br.com.uol.ps.core.api.vo.StateVO;
import br.com.uol.ps.core.api.vo.VenuesPaymentCheckResponseVO;
import br.com.uol.ps.core.api.vo.VenuesPaymentRequestVO;
import br.com.uol.ps.core.api.vo.VenuesPaymentResponseVO;
import br.com.uol.ps.core.api.vo.VenuesWalletTransactionDetailsVO;
import br.com.uol.ps.core.api.vo.VersionResponseVO;
import br.com.uol.ps.core.api.vo.WalletCreditCardVO;
import br.com.uol.ps.core.storage.DataStorageApp;
import br.com.uol.ps.core.api.vo.PlacesResponseVO;
import br.com.uol.ps.core.api.vo.UserDetailsResponseVO;
import br.com.uol.ps.core.util.PagSegUtil;
import br.com.uol.ps.wallet.BuildConfig;
import br.com.uol.ps.wallet.WalletApplication;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.client.UrlConnectionClient;

/**
 * Created by everton on 05/06/14.
 */
@Singleton
public class RestClient {


    private static final String PREFIX_URL = BuildConfig.BASE_URL;
    private static final String PREFIX_URL_DF = BuildConfig.BASE_URL_DF;

    private Map<String, StateVO> STATES = new HashMap<String, StateVO>();
    private Map<String, StateVO> STATES_ACRONYM = new HashMap<String, StateVO>();

    private final Context context;
    private final WalletApplication app;
    private final RestWallet rest;
    private final RestDFWallet restDF;
    private Gson mGson;

    public RestClient(Context context) {
        System.setProperty("http.keepAlive", "false");
        System.setProperty("http.connection.stalecheck", "true");
        this.context = context.getApplicationContext();

        this.app = ((WalletApplication) this.context);

        GsonBuilder gb = new GsonBuilder();
        gb.excludeFieldsWithModifiers(Modifier.TRANSIENT);

        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(PREFIX_URL)

                .setClient(new RetrofitHttpClient())
                .setConverter(new GzipGsonConverter(gb.create()))
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade requestFacade) {
                        if (!BuildConfig.DEBUG) {
                            requestFacade.addHeader("Accept-Encoding", "gzip");
                        }
                        /*requestFacade.addHeader("Device", "put here some device information");*/
                    }
                });

        rest = builder.build().create(RestWallet.class);
        restDF = builder.setEndpoint(PREFIX_URL_DF).build().create(RestDFWallet.class);

        app.setRest(this);
    }


    public <T extends ResponseInterface> void execute(WalletRequestCallback<T> cb) {
        try {
            int timeoutForLoading = cb.onDoExecute();
            cb.showLoadingMessage(timeoutForLoading);
        } catch (Throwable t) {
            cb.failure(null);
        }

    }

    public void cart(long placeId, String cartId, Callback<CartResponseVO> cb) {
        rest.cart(placeId, cartId, getToken(), cb);
    }

    public CartResponseVO cart(long placeId, String cartId) {
        return rest.cart(placeId, cartId, getToken());
    }

    public void places(Callback<PlacesResponseVO> cb) {
        rest.places(getToken(), cb);
    }

    public void history(Callback<HistoryWalletTransactionResponseVO> cb) {
        rest.history(getToken(), cb);
    }

    public void wallet(Callback<CreditCardResponseVO> cb) {
        rest.wallet(getToken(), cb);
    }

    public void startDFSession(Callback<DFStartSessionResponseVO> cb) {
        rest.startDFSession(getToken(), cb);
    }

    public DFStartSessionResponseVO startDFSession() {
        return rest.startDFSession(getToken());
    }

    public void userDetails(Callback<UserDetailsResponseVO> cb) {
        rest.userDetails(getToken(), cb);
    }

    public void modulusExponent(String dfSessionToken, String owner, Callback<ModulusExponentVO> cb) {
        restDF.modulusExponent(dfSessionToken, owner, "pagseguro-checkout-wallet", cb);
    }

    public ModulusExponentVO modulusExponent(String dfSessionToken, String owner) {
        return restDF.modulusExponent(dfSessionToken, owner, "pagseguro-checkout-wallet");
    }

    public void getTokenDurableAddCard(String dfSessionToken, String owner, String ccCrypt, String creditCardBrand, String expMonth, String expYear, Callback<BaseDFResponseVO> cb) {
        restDF.durableToken(dfSessionToken, owner, creditCardBrand, expMonth, expYear.length() == 2 ? "20" + expYear : expYear, "true", ccCrypt, "pagseguro-checkout-d-newcard", cb);
    }

    public BaseDFResponseVO getTokenDurableAddCard(String dfSessionToken, String owner, String ccCrypt, String creditCardBrand, String expMonth, String expYear) {
        return restDF.durableToken(dfSessionToken, owner, creditCardBrand, expMonth, expYear.length() == 2 ? "20" + expYear : expYear, "true", ccCrypt, "pagseguro-checkout-d-newcard");
    }

    public void persistCard(WalletCreditCardVO creditCardVO, Callback<AddCreditCardResponseVO> cb) {
        rest.persistCard(getToken(), creditCardVO, cb);
    }

    public AddCreditCardResponseVO persistCard(WalletCreditCardVO creditCardVO) {
        return rest.persistCard(getToken(), creditCardVO);
    }

    public void getTokenDurableCheckout(String dfSessionToken, String owner, String cvv, String creditCardToken, Callback<BaseDFResponseVO> cb) {
        restDF.durableTokenCheckout(dfSessionToken, owner, cvv, creditCardToken, "pagseguro-checkout-wallet", cb);
    }

    public BaseDFResponseVO getTokenDurableCheckout(String dfSessionToken, String owner, String cvv, String creditCardToken) {
        return restDF.durableTokenCheckout(dfSessionToken, owner, cvv, creditCardToken, "pagseguro-checkout-wallet");
    }

    public void paymentPlace(VenuesPaymentRequestVO requestVO, Callback<VenuesPaymentResponseVO> cb) {
        rest.paymentPlace(getToken(), requestVO, cb);
    }

    public void payment(VenuesPaymentRequestVO requestVO, Callback<VenuesPaymentResponseVO> cb) {
        rest.payment(getToken(), requestVO, cb);
    }

    private BaseResponseVO isValidToken = null;
    public boolean isTokenValid() {
        isValidToken = null;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                isValidToken = rest.checkToken(getToken());
            }
        });

        t.start();
        try {
            t.join();
        } catch (Exception ex) {
            return false;
        }

        return isValidToken != null ? isValidToken.isSuccess() : false;
    }

    PlacesResponseVO places = null;
    public PlaceVO placeByEmail(String email) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                places = rest.places(getToken());
            }
        });

        t.start();
        try {
            t.join();
        } catch (Exception ex) {
            return null;
        }

        if (places != null && places.getPlaces() != null) {
            for(PlaceVO place : places.getPlaces()) {
                if (place.getEmail() != null && place.getEmail().equals(email)) {
                    return place;
                }
            }
        }
        return null;
    }

    public VenuesPaymentResponseVO paymentPlace(VenuesPaymentRequestVO requestVO) {
        return rest.paymentPlace(getToken(), requestVO);
    }

    public VenuesPaymentResponseVO payment(VenuesPaymentRequestVO requestVO) {
        return rest.payment(getToken(), requestVO);
    }

    public CardBrandResponseVO cardBrand(String creditCard, String dfSessionToken, String dfSessionOwner) {
        return restDF.cardBrand(creditCard, dfSessionToken, dfSessionOwner, "pagseguro-checkout-wallet");
    }

    public void login(SetupVO setupVO, Callback<SetupResponseVO> cb) {
        rest.login(setupVO, cb);
    }

    public VenuesPaymentCheckResponseVO checkPayment(String transactionCode) {
        return rest.checkPayment(getToken(), transactionCode);
    }

    public void deleteCard(String walletId, Callback<BaseResponseVO> cb) {
        Map<String, String> body = new HashMap<String, String>();
        body.put("creditCardId", walletId);
        rest.deleteCard(getToken(), body, cb);
    }

    public VersionResponseVO version() {
        return rest.version("" + new Random().nextInt());
    }

    public void version(Callback<VersionResponseVO > cb) {
        rest.version("" + new Random().nextInt(), cb);
    }

    public VenuesWalletTransactionDetailsVO paymentDetails(String transactionCode) {
        return rest.paymentDetails(getToken(), transactionCode);
    }

    public void sendPushToken(String pushToken, Callback<BaseResponseVO> cb) {
        Map<String, String> body = new HashMap<String, String>();
        body.put("platform", "android");
        body.put("platformVersion", Build.VERSION.RELEASE);
        body.put("deviceModel", "Nexus 7");
        body.put("appVersion", BuildConfig.VERSION_NAME);
        body.put("deviceID", PagSegUtil.getDeviceIdentifier(context));
        body.put("pushToken", pushToken);

        try {
            rest.sendPushToken(getToken(), body, cb);
        } catch (Exception ex) {
            cb.failure(null);
        }
    }

    public BaseResponseVO sendPushToken(String pushToken) {
        Map<String, String> body = new HashMap<String, String>();
        body.put("platform", "android");
        body.put("platformVersion", Build.VERSION.RELEASE);
        body.put("deviceModel", PagSegUtil.getDeviceModel(context));
        body.put("appVersion", BuildConfig.VERSION_NAME);
        body.put("deviceID", PagSegUtil.getDeviceIdentifier(context));
        body.put("pushToken", pushToken);

        try {
            return rest.sendPushToken(getToken(), body);
        } catch (Exception ex) {
            return null;
        }
    }

    public PostalCodeResponseVO getAddress(String zip) {
        return rest.getAddress(getToken(), zip);
    }



    /*
     *  Helpers de Endereços
     *
     */
    public List<StateVO> getStates() {
        List<StateVO> result = new ArrayList<StateVO>(STATES.values());

        if (result == null || result.isEmpty()) {

            try {
                InputStream statesFile = this.app.getAssets().open("states/br_states.json", AssetManager.ACCESS_BUFFER);
                Type listType = new TypeToken<List<StateVO>>() { }.getType();
                result = getParser().fromJson(new InputStreamReader(statesFile), listType);

                for (StateVO state : result) {
                    STATES.put(state.getId(), state);
                    STATES_ACRONYM.put(state.getAcronym().toUpperCase(), state);
                }

                List<CityVO> cities = getCities();
                for (CityVO city : cities) {
                    STATES.get(city.getStateId()).addCity(city);
                }

                Log.d("States", STATES_ACRONYM.toString());

            } catch (IOException e) { }

        }

        return result;
    }

    public List<String> getStatesAcronym() {
        checkStatesNull();

        List<String> result = new ArrayList<String>();
        List<StateVO> states = getStates();
        for (StateVO state : states) {
            result.add(state.getAcronym());
        }

        return result;
    }

    public List<CityVO> getCities() {
        checkStatesNull();
        List<CityVO> result = null;

        try {
            InputStream statesFile = this.app.getAssets().open("states/br_cities.json", AssetManager.ACCESS_BUFFER);
            Type listType = new TypeToken<List<CityVO>>() {
            }.getType();
            result = getParser().fromJson(new InputStreamReader(statesFile), listType);

        } catch (IOException e) {
        }

        return result;
    }

    public StateVO getStateByAcronym(String acronym) {
        checkStatesNull();
        return STATES_ACRONYM.get(acronym);
    }

    private void checkStatesNull() {
        if (STATES == null || STATES_ACRONYM == null || STATES.isEmpty() || STATES_ACRONYM.isEmpty()) {
            getStates();
        }
    }

    public Gson getParser() {
        Gson result = mGson;

        if (result == null) {
            GsonBuilder gb = new GsonBuilder();
            mGson = gb.create();
            result = mGson;
        }

        return result;
    }


    /*
     * @see http://stackoverflow.com/questions/23893113/nosuchmethoderror-if-i-am-using-okhttp-2-0-and-the-latest-retrofit
     */
    private String getToken() {
        return DataStorageApp.getToken(this.app);
    }

    private static class RetrofitHttpClient extends UrlConnectionClient {
        private static final int CONNECT_TIMEOUT_MILLIS = 60 * 1000; // 30s

        private static final int READ_TIMEOUT_MILLIS = 85 * 1000; // 45s

        private static OkUrlFactory generateDefaultOkUrlFactory() {
            OkHttpClient client = new com.squareup.okhttp.OkHttpClient();
            client.setConnectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            client.setReadTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            return new OkUrlFactory(client);
        }

        private final OkUrlFactory factory;

        public RetrofitHttpClient() {
            factory = generateDefaultOkUrlFactory();
        }
        @Override
        protected HttpURLConnection openConnection(Request request) throws IOException {

            HttpURLConnection conn = factory.open(new URL(request.getUrl()));

            if (conn instanceof HttpsURLConnection) {
                ((HttpsURLConnection) conn).setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            }
            return conn;
        }

    }

}
