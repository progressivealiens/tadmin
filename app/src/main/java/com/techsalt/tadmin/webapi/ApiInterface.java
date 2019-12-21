package com.techsalt.tadmin.webapi;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("adminLoginOnMobile")
    Call<ApiResponse> Login(
            @Field("email") String email,
            @Field("password") String password
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

}
