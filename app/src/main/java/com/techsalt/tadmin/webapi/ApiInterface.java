package com.techsalt.tadmin.webapi;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("adminVerifiedLoginOnMobile")
    Call<ApiResponse> Login(
            @Field("token") String token,
            @Field("companyEmail") String companyEmail,
            @Field("adminEmail") String adminEmail,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("getAnalyticsData")
    Call<BarGraphResponse> BarGraph(
            @Field("companyName") String companyName
    );



    @FormUrlEncoded
    @POST("findAllTypeCategory")
    Call<ApiResponse> findAllTypeCategory(
            @Field("companyName") String companyName
    );

    @FormUrlEncoded
    @POST("findAllOperationDetails")
    Call<ApiResponse> findAllOperationDetails(
            @Field("companyName") String companyName,
            @Field("dateSearchData") String dateSearchData,
            @Field("category") String category
    );

    @FormUrlEncoded
    @POST("findAllRoute")
    Call<ApiResponse> findAllRoute(
            @Field("companyName") String companyName
    );

    @FormUrlEncoded
    @POST("findAllGuardDetails")
    Call<ApiResponse> findAllGuardDetails(
            @Field("companyName") String companyName,
            @Field("dateSearchData") String dateSearchData,
            @Field("routeId") String routeId
    );


    @FormUrlEncoded
    @POST("findAllAbsentOperationDetails")
    Call<ApiResponse> findAllAbsentOperationDetails(
            @Field("companyName") String companyName,
            @Field("dateSearchData") String dateSearchData,
            @Field("category") String category
    );

    @FormUrlEncoded
    @POST("employeeFlagDetails")
    Call<ApiResponse> employeeFlagDetails(
            @Field("employeeId") String category
    );


    @FormUrlEncoded
    @POST("siteVisitHistory")
    Call<SiteVisitRes> siteVisitHistory(
            @Field("companyName") String companyName,
            @Field("dateSearchData") String dateSearchData
    );


    @FormUrlEncoded
    @POST("sendDarEmailReportToClientFromMobile")
    Call<SiteVisitRes> sendDarEmailReportToClient(
            @Field("adminId") String adminId,
            @Field("suid") String suid,
            @Field("euid") String euid,
            @Field("survey") String survey
    );

    @FormUrlEncoded
    @POST("getSiteListOfCompany")
    Call<AllSiteListResponse> getSiteListOfCompany(
            @Field("companyName") String companyName
    );

    @FormUrlEncoded
    @POST("sendPatrolEmailReportToClientFromMobile")
    Call<SiteVisitRes> sendPatrolEmailReportToClient(
            @Field("adminId") String adminId,
            @Field("euid") String euid,
            @Field("siteId") String siteId,
            @Field("clientId") String clientId,
            @Field("patrol") String survey
    );

    @GET("{latitude},{longitude}?exclude=minutely,hourly,daily,alerts,flags")
    Call<WeatherApiResponse> fetchTemp(
            @Path("latitude") String latitude,
            @Path("longitude") String longitude
    );


    @FormUrlEncoded
    @POST("adminLogout")
    Call<ApiResponse> Logout(
            @Field("adminId") String adminId
    );

}
