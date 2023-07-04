package com.chat.befiler.retrofit;

import com.chat.befiler.model.chat.AgentsByGroupIdModel;
import com.chat.befiler.model.chat.Conversation;
import com.chat.befiler.model.chat.ConversationByUID;
import com.chat.befiler.model.chat.ConversationsCount;
import com.chat.befiler.model.chat.FilesData;
import com.chat.befiler.model.chat.GroupsDataModel;
import com.chat.befiler.model.chat.UploadFilesData;
import com.chat.befiler.model.chat.UserProfile;
import com.chat.befiler.model.login.LoginRequest;
import com.chat.befiler.model.login.LoginResponseData;
import com.chat.befiler.model.login.decodeTokenModel.GroupsData;

import java.lang.reflect.Array;
import java.util.ArrayList;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WebService {

    @POST("Account/Login")
    Call<WebResponse<LoginResponseData>> loginUser(@Body LoginRequest loginRequest);

    @GET("Chat/GetConversations")
    Call<WebResponse2<ArrayList<Conversation>>> getConversationList(@Query("PageNumber") int PageNumber,
                                                                   @Query("PageSize")int PageSize,
                                                                   @Query("agentId") String agentId,
                                                                   @Query("isAdmin") String isAdmin,
                                                                    @Query("status") int status);

    @GET("Chat/GetConversationsByUId")
    Call<WebResponse2<ArrayList<ConversationByUID>>> getConversationByUID(@Query("pageNumber") int pageNumber,
                                                                         @Query("pageSize") int pageSize,
                                                                         @Query("conversationUId") String conversationUId,
                                                                         @Query("agentId") String agentId);
    @Multipart
    @POST("Chat/UploadFiles")
    Call<WebResponse<ArrayList<UploadFilesData>>> uploadFiles(@Query("conversationUId")
                                                     String conversationUId, @Part ArrayList<MultipartBody.Part> files);

//    @GET("UserManagement/GetUserById?UserId/{id}")
//    Call<WebResponse<UserProfile>> userProfileData(@Path("id") String id);

     @GET("UserManagement/GetUserById")
     Call<WebResponse<UserProfile>> getUserById(@Query("userId") String userId);


    @GET("UserManagement/GetAllGroups")
    Call<WebResponse<ArrayList<GroupsDataModel>>> getAllGroups(@Query("isActive") boolean isActive);

    @GET("UserManagement/GetGroupByUser")
    Call<WebResponse<GroupsByUserDataModel>> getGroupsByUserId(@Query("userId") int userId);

    @GET("UserManagement/GetAgentsByGroupId")
    Call<WebResponse<ArrayList<AgentsByGroupIdModel>>> getAgentsByGroupId(@Query("groupId") int groupId);

    @GET("Chat/GetConversationsCount")
    Call<WebResponse<ArrayList<ConversationsCount>>> getConversationCount(@Query("userId") int userId,@Query("isAdmin") boolean isAdmin);




}