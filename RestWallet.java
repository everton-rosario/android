package br.com.uol.ps.core.api;

import java.util.Map;

import br.com.uol.ps.core.api.vo.AddCreditCardResponseVO;
import br.com.uol.ps.core.api.vo.BaseResponseVO;
import br.com.uol.ps.core.api.vo.CartResponseVO;
import br.com.uol.ps.core.api.vo.CreditCardResponseVO;
import br.com.uol.ps.core.api.vo.DFStartSessionResponseVO;
import br.com.uol.ps.core.api.vo.HistoryWalletTransactionResponseVO;
import br.com.uol.ps.core.api.vo.PostalCodeResponseVO;
import br.com.uol.ps.core.api.vo.SetupResponseVO;
import br.com.uol.ps.core.api.vo.SetupVO;
import br.com.uol.ps.core.api.vo.VenuesPaymentCheckResponseVO;
import br.com.uol.ps.core.api.vo.VenuesPaymentRequestVO;
import br.com.uol.ps.core.api.vo.VenuesPaymentResponseVO;
import br.com.uol.ps.core.api.vo.VenuesWalletTransactionDetailsVO;
import br.com.uol.ps.core.api.vo.VersionResponseVO;
import br.com.uol.ps.core.api.vo.WalletCreditCardVO;
import br.com.uol.ps.core.api.vo.PlacesResponseVO;
import br.com.uol.ps.core.api.vo.UserDetailsResponseVO;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by everton on 05/06/14.
 */
public interface RestWallet {

    /* ===============================================================
     * Syncronized services API.
     * For use it need to be runned on another thread than UIThread
     */
    @POST("/login")
    SetupResponseVO login(@Body SetupVO setupVO);

    @GET("/places")
    PlacesResponseVO places(@Query("token") String token);

    @GET("/place/{placeId}/cart/{cartId}")
    CartResponseVO cart(@Path("placeId") long placeId, @Path("cartId") String cartId, @Query("token") String token);

    @GET("/transactions")
    HistoryWalletTransactionResponseVO history(@Query("token") String token);

    @GET("/wallet")
    CreditCardResponseVO wallet(@Query("token") String token);

    @GET("/startsession")
    DFStartSessionResponseVO startDFSession(@Query("token") String token);

    @GET("/userDetails")
    UserDetailsResponseVO userDetails(@Query("token") String token);

    @POST("/persistCreditCardWallet")
    AddCreditCardResponseVO persistCard(@Query("token") String token, @Body WalletCreditCardVO creditCardVO);

    @POST("/payment/place")
    VenuesPaymentResponseVO paymentPlace(@Query("token") String token, @Body VenuesPaymentRequestVO requestVO);

    @POST("/payment")
    VenuesPaymentResponseVO payment(@Query("token") String token, @Body VenuesPaymentRequestVO requestVO);

    @GET("/transaction/{transaction}")
    VenuesPaymentCheckResponseVO checkPayment(@Query("token") String token, @Path("transaction") String transactionCode);

    @GET("/transaction/{transaction}/result")
    VenuesWalletTransactionDetailsVO paymentDetails(@Query("token") String token, @Path("transaction") String transactionCode);

    @POST("/removeCreditCardWallet")
    BaseResponseVO deleteCard(@Query("token") String token, @Body Map<String, String> body);

    @GET("/version")
    VersionResponseVO version(@Query("nocache") String nocache);

    @POST("/activationDetails")
    BaseResponseVO sendPushToken(@Query("token") String token, @Body Map<String, String> body);

    @GET("/addressDetails")
    PostalCodeResponseVO getAddress(@Query("token") String token, @Query("postalCode") String zip);

    @GET("/check-token")
    BaseResponseVO checkToken(@Query("token") String token);


    /* ===============================================================
     * Asynchronous services API.
     * It can be used on UIThread, using the callback parameter
     */
    @POST("/login")
    void login(@Body SetupVO setupVO, Callback<SetupResponseVO> cb);

    @GET("/places")
    void places(@Query("token") String token, Callback<PlacesResponseVO> cb);

    @GET("/place/{placeId}/cart/{cartId}")
    void cart(@Path("placeId") long placeId, @Path("cartId") String cartId, @Query("token") String token, Callback<CartResponseVO> cb);

    @GET("/transactions")
    void history(@Query("token") String token, Callback<HistoryWalletTransactionResponseVO> cb);

    @GET("/wallet")
    void wallet(@Query("token") String token, Callback<CreditCardResponseVO> cb);

    @GET("/startsession")
    void startDFSession(@Query("token") String token, Callback<DFStartSessionResponseVO> cb);

    @GET("/userDetails")
    void userDetails(@Query("token") String token, Callback<UserDetailsResponseVO> cb);

    @POST("/persistCreditCardWallet")
    void persistCard(@Query("token") String token, @Body WalletCreditCardVO creditCardVO, Callback<AddCreditCardResponseVO> cb);

    @POST("/payment/place")
    void paymentPlace(@Query("token") String token, @Body VenuesPaymentRequestVO requestVO, Callback<VenuesPaymentResponseVO> cb);

    @POST("/payment")
    void payment(@Query("token") String token, @Body VenuesPaymentRequestVO requestVO, Callback<VenuesPaymentResponseVO> cb);

    @GET("/transaction/{transaction}")
    void checkPayment(@Query("token") String token, @Path("transaction") String transactionCode, Callback<VenuesPaymentCheckResponseVO> cb);

    @GET("/transaction/{transaction}/result")
    void paymentDetails(@Query("token") String token, @Path("transaction") String transactionCode, Callback<VenuesWalletTransactionDetailsVO> cb);

    @POST("/removeCreditCardWallet")
    void deleteCard(@Query("token") String token, @Body Map<String, String> body, Callback<BaseResponseVO> cb);

    @GET("/version")
    void version(@Query("nocache") String nocache, Callback<VersionResponseVO> cb);

    @POST("/activationDetails")
    void sendPushToken(@Query("token") String token, @Body Map<String, String> body, Callback<BaseResponseVO> cb);

    @GET("/addressDetails")
    void getAddress(@Query("token") String token, @Query("postalCode") String zip, Callback<PostalCodeResponseVO> cb);

    @GET("/check-token")
    void checkToken(@Query("token") String token, Callback<BaseResponseVO> cb);
}
