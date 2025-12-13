package com.example.midiventaslvlup.network

import com.example.midiventaslvlup.network.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ============================================
    // AUTH ENDPOINTS
    // ============================================

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<Map<String, Any>>


    // ============================================
    // USER MANAGEMENT ENDPOINTS
    // ============================================

    @POST("/api/auth/register")
    suspend fun createUser(@Body request: RegisterRequest): ApiResponse<Map<String, Any>>

    @GET("/api/users")
    suspend fun getUsers(): ApiResponse<List<UserResponse>>

    @GET("/api/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): ApiResponse<UserResponse>

    @GET("/api/users/stats")
    suspend fun getUserStats(): ApiResponse<StatsResponse>

    // CORREGIDO: El @Body ahora es un Map<String, String> para coincidir con el backend
    @PUT("/api/users/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body updateData: Map<String, String>): ApiResponse<UserResponse>

    @PUT("/api/users/{id}/change-role")
    suspend fun changeUserRole(@Path("id") id: Long, @Body roleData: Map<String, String>): ApiResponse<UserResponse>

    @DELETE("/api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<ApiResponse<Unit>>


    // ============================================
    // PRODUCT ENDPOINTS
    // ============================================

    @GET("/api/products")
    suspend fun getAllProducts(): ApiResponse<List<ProductDto>>

    @GET("/api/products/available")
    suspend fun getProductsWithStock(): ApiResponse<List<ProductDto>>

    @GET("/api/products/{id}")
    suspend fun getProductById(@Path("id") id: Long): ApiResponse<ProductDto>

    @GET("/api/products/category/{categoria}")
    suspend fun getProductsByCategory(@Path("categoria") categoria: String): ApiResponse<List<ProductDto>>

    @GET("/api/products/categories")
    suspend fun getAllCategories(): ApiResponse<List<String>>

    @GET("/api/products/search")
    suspend fun searchProducts(@Query("name") name: String): ApiResponse<List<ProductDto>>

    @GET("/api/products/price-range")
    suspend fun getProductsByPriceRange(
        @Query("min") min: Int,
        @Query("max") max: Int
    ): ApiResponse<List<ProductDto>>


    // ============================================
    // CART ENDPOINTS
    // ============================================

    @GET("/api/cart/{userId}")
    suspend fun getCart(@Path("userId") userId: Long): ApiResponse<CartDto?>

    @GET("/api/cart/{userId}/details")
    suspend fun getCartDetails(@Path("userId") userId: Long): ApiResponse<CartDto>

    @POST("/api/cart/{userId}/add")
    suspend fun addProductToCart(
        @Path("userId") userId: Long,
        @Body request: AddToCartRequest
    ): ApiResponse<CartDto>

    @POST("/api/cart/{userId}/increase")
    suspend fun increaseQuantity(
        @Path("userId") userId: Long,
        @Body request: Map<String, Long>
    ): ApiResponse<CartDto>

    @POST("/api/cart/{userId}/decrease")
    suspend fun decreaseQuantity(
        @Path("userId") userId: Long,
        @Body request: Map<String, Long>
    ): ApiResponse<CartDto>

    @PUT("/api/cart/{userId}/update-quantity")
    suspend fun updateQuantity(
        @Path("userId") userId: Long,
        @Body request: UpdateQuantityRequest
    ): ApiResponse<CartDto>

    @DELETE("/api/cart/{userId}/remove/{productId}")
    suspend fun removeProductFromCart(
        @Path("userId") userId: Long,
        @Path("productId") productId: Long
    ): ApiResponse<CartDto>

    @DELETE("/api/cart/{userId}/clear")
    suspend fun clearCart(@Path("userId") userId: Long): ApiResponse<CartDto>

    @GET("/api/cart/{userId}/count")
    suspend fun getCartItemCount(@Path("userId") userId: Long): ApiResponse<Int>

    @GET("/api/cart/{userId}/items")
    suspend fun getCartItems(@Path("userId") userId: Long): ApiResponse<List<CartItemDto>>


    // ============================================
    // ORDER ENDPOINTS
    // ============================================

    @POST("/api/orders/create")
    suspend fun createOrder(@Body request: CreateOrderRequest): ApiResponse<OrderDto>

    @POST("/api/orders/process-payment")
    suspend fun processPayment(@Body request: CreateOrderRequest): ApiResponse<OrderDto>

    @GET("/api/orders/{orderId}")
    suspend fun getOrderById(@Path("orderId") orderId: Long): ApiResponse<OrderDto>

    @GET("/api/orders/user/{userId}")
    suspend fun getOrdersByUser(@Path("userId") userId: Long): ApiResponse<List<OrderDto>>

    @GET("/api/orders/user/{userId}/history")
    suspend fun getUserOrderHistory(@Path("userId") userId: Long): ApiResponse<Map<String, Any>>


    /**
     * Crear un nuevo producto (admin)
     * POST /api/products
     */
    @POST("/api/products")
    suspend fun createProduct(@Body productData: ProductRequest): ApiResponse<ProductDto>

    /**
     * Actualizar un producto (admin)
     * PUT /api/products/{id}
     */
    @PUT("/api/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Long,
        @Body productData: ProductRequest
    ): ApiResponse<ProductDto>

    /**
     * Actualizar solo el stock de un producto
     * PATCH /api/products/{id}/stock
     */
    @PATCH("/api/products/{id}/stock")
    suspend fun updateProductStock(
        @Path("id") id: Long,
        @Body stockData: Map<String, Int>
    ): ApiResponse<ProductDto>

    /**
     * Eliminar un producto (admin)
     * DELETE /api/products/{id}
     */
    @DELETE("/api/products/{id}")
    suspend fun deleteProduct(@Path("id") id: Long): ApiResponse<Unit>
}
