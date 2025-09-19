# 🤝 README

This project provides a comprehensive API for managing various aspects of the National Youth Service Corps (NYSC) program, with a focus on **Places of Primary Assignment (PPAs)**. The API supports a wide range of administrative and user-specific tasks, from managing user credentials to handling clearance documents.

---

## 🔑 Authentication

All API endpoints are protected and require a valid **Bearer token** for access. This ensures that only authenticated and authorized users can interact with the system's functionalities.

### Authentication Endpoints

These endpoints are for managing user sessions and credentials.

* `POST /api/v1/auth/login`: Authenticates a user and returns JWT tokens.
* `POST /api/v1/auth/setup-password`: Allows a new user to set their password using a setup token.
* `GET /api/v1/auth/validate-setup-token`: Checks if a setup token is valid and not expired.
* `POST /api/v1/auth/forgot-password`: Initiates the password reset process by sending a link to the user's email.
* `GET /api/v1/auth/reset-password/validate`: Validates a password reset token before allowing a password change.
* `POST /api/v1/auth/reset-password`: Resets a user's password using a valid reset token.
* `POST /api/v1/auth/resend-setup`: Resends a password setup email to a user who hasn’t set one yet.
* `PUT /api/v1/user/credentials`: Updates the credentials of the authenticated user.
* `POST /api/v1/auth/refresh`: Refreshes JWT tokens using a refresh token.
* `POST /api/v1/auth/logout`: Logs out a single device by invalidating its refresh token.
* `POST /api/v1/auth/logout-all`: Logs the user out of all active sessions.

---

## 🗂️ API Endpoints

The API is logically grouped into several categories to provide clear and organized access to different functionalities.

### **Admins**

These endpoints are for managing **Admin** accounts and profiles. Certain actions are restricted to **Super Admins**.

* `POST /api/v1/admins`: Creates a new **Admin** account. This requires a **SUPER_ADMIN** role.
* `GET /api/v1/admins`: Retrieves a list of all **Admins**. This requires a **SUPER_ADMIN** role.
* `GET /api/v1/admins/me`: Retrieves the profile of the currently authenticated **Admin**.
* `GET /api/v1/admins/{adminId}`: Retrieves the details of an **Admin** by their ID. This requires a **SUPER_ADMIN** role.
* `GET /api/v1/admins/{adminId}/unit-id`: Retrieves the **Unit ID** associated with a given **Admin**. This requires a **SUPER_ADMIN** role.
* `PUT /api/v1/admins/{adminId}`: Updates an **Admin's** profile by their ID. This requires a **SUPER_ADMIN** role.
* `DELETE /api/v1/admins/{adminId}`: Deletes an **Admin** account by ID. This requires a **SUPER_ADMIN** role.

---

### **Clearance Controllers**

These endpoints manage the clearance request process for **Corp Members**, **Unit Heads**, and **Super Admins**.

* `POST /api/v1/clearances/requests`: Allows a **Corper** to create a new clearance request for a given month.
* `GET /api/v1/clearances/requests`: Fetches all clearance requests created by the currently authenticated **Corper**.
* `PATCH /api/v1/clearances/requests/{id}/cancel`: Allows a **Corper** to cancel a pending clearance request.
* `GET /api/v1/clearances/unit-head/requests`: Fetches all clearance requests submitted within a unit.
* `POST /api/v1/clearances/unit-head/approve/{requestId}`: Allows a **Unit Head** to approve a clearance request within their unit.
* `POST /api/v1/clearances/unit-head/reject/{requestId}`: Allows a **Unit Head** to reject a clearance request with a specified reason.
* `GET /api/v1/clearances/ppa/requests`: Fetches all clearance requests associated with the **Super Admin’s** PPA.
* `POST /api/v1/clearances/super-admin/approve/{requestId}`: Allows a **Super Admin** to approve a clearance request.
* `POST /api/v1/clearances/super-admin/reject/{requestId}`: Allows a **Super Admin** to reject a clearance request with a specified reason.

---

### **Clearance Documents**

These endpoints are for generating and managing clearance documents.

* `GET /api/v1/clearance-docs/{requestId}/view`: Streams a clearance letter as a PDF for a given request ID. Restricted to **SUPER_ADMIN**.
* `GET /api/v1/clearance-docs/{requestId}/download`: Downloads a clearance letter as a PDF for a given request ID. Restricted to **SUPER_ADMIN**.

---

### **Corp Members**

These endpoints are for managing **Corp Member** accounts, profiles, and assignments.

* `POST /api/v1/corpers`: Registers a new **Corper**. This is restricted to **ADMIN** and **SUPER_ADMIN** roles.
* `GET /api/v1/corpers/me`: Retrieves the profile of the currently authenticated **Corper**.
* `PUT /api/v1/corpers/me`: Updates the profile of the currently authenticated **Corper**.
* `GET /api/v1/corpers/{id}`: Retrieves a **Corper's** profile by ID. This is accessible to **Admins**.
* `PUT /api/v1/corpers/{id}`: Updates a **Corper's** profile by ID. This is accessible to **Admins**.
* `DELETE /api/v1/corpers/{id}`: Deletes a **Corper** account. This is restricted to **Admins** and **Super Admins**.
* `GET /api/v1/corpers/unit/{unitId}`: Retrieves all **Corpers** within a specific **Unit**. This is accessible to **Admins**.
* `GET /api/v1/corpers/ppa/{ppaId}`: Retrieves all **Corpers** within a specific **PPA**. This is restricted to **Super Admins**.
* `PUT /api/v1/corpers/{id}/unblock`: Unblocks a **Corper** account. This is restricted to **Super Admins**.
* `PUT /api/v1/corpers/{id}/block`: Blocks a **Corper** account. This is restricted to **Super Admins**.

---

### **PPAs**

These endpoints are for managing **Places of Primary Assignment (PPAs)**. Access is restricted to **Global Admins**.

* `POST /api/v1/ppas`: Creates a new **PPA**.
* `GET /api/v1/ppas`: Fetches a list of all **PPAs**.
* `GET /api/v1/ppas/{id}`: Fetches a specific **PPA** by its ID.
* `PUT /api/v1/ppas/{id}`: Updates the details of a specific **PPA** by ID.
* `DELETE /api/v1/ppas/{id}`: Deletes a specific **PPA** by ID.

---

### **Resources**

This endpoint provides general resources for the application.

* `GET /api/v1/resources/clearance-months`: Retrieves a list of available months for clearance requests.

---

### **Super Admins**

These endpoints are for managing **Super Admin** accounts. Some actions are restricted to **Global Admins**.

* `POST /api/v1/super-admin`: Creates a new **Super Admin** account. This is restricted to **Global Admins**.
* `GET /api/v1/super-admin/me`: Retrieves the profile of the currently authenticated **Super Admin**.
* `PUT /api/v1/super-admin/me`: Updates the profile of the currently authenticated **Super Admin**.
* `GET /api/v1/super-admin/{id}`: Fetches the profile of a **Super Admin** by their ID. This requires a **GLOBAL_ADMIN** role.
* `PUT /api/v1/super-admin/{id}`: Updates a **Super Admin's** profile by their user ID.
* `DELETE /api/v1/super-admin/{id}`: Deletes a **Super Admin** account by ID. This requires a **SUPER_ADMIN** role.

---

### **Units**

These endpoints are for managing **Units** within **PPAs** and are restricted to users with the **SUPER_ADMIN** role.

* `POST /api/v1/units`: Creates a new **Unit** under a PPA.
* `GET /api/v1/units/{id}`: Retrieves the details of a specific **Unit** by its ID.
* `PUT /api/v1/units/{id}`: Updates an existing **Unit** using its ID.
* `DELETE /api/v1/units/{id}`: Deletes a **Unit** by its ID.
* `GET /api/v1/units/ppa/{ppaId}`: Retrieves all **Units** belonging to a specific **PPA**.

---

## 🖥️ Schema Definitions

The API uses specific schemas for requests and responses.

### Request DTOs (Data Transfer Objects)

* `AdminRequestDto`: Request body for creating a new **Admin**.
* `AuthenticationRequestDto`: Request body for user login, including `email` and `password`.
* `CancelClearanceRequestDto`: Request body for canceling a clearance request, including `request_id` and `cancellation_reason`.
* `ClearanceRequestDto`: Request body for creating a new clearance request, including `tentative_date` and `clearance_month`.
* `CorperRequestDto`: Request body for creating or updating a **Corper**.
* `ForgotPasswordRequestDto`: Request body for initiating a password reset, with the user's `email`.
* `PpaRequestDto`: Request body for creating a new **PPA**, with `ppa_name` and `ppa_address`.
* `RefreshTokenRequest`: Request body for refreshing tokens or logging out, containing a `refresh_token`.
* `RejectClearanceRequestDto`: Request body for rejecting a clearance request, with a `reason`.
* `ResendSetupDto`: Request body for resending a password setup email, containing the user's `email`.
* `ResetPasswordRequestDto`: Request body for resetting a password, with `reset_token` and `new_password`.
* `SetupPasswordRequestDto`: Request body for setting a new password, with `password`, `confirm_password`, and `setup_token`.
* `SuperAdminRequestDto`: Request body for creating a new **Super Admin**.
* `UnitRequestDto`: Request body for creating or updating a **Unit**.
* `UpdateAdminRequestDto`: Request body for updating an **Admin** profile.
* `UpdateCredentialsRequestDto`: Request body for updating user credentials.
* `UpdatePpaRequestDto`: Request body for updating a **PPA**.
* `UpdateSuperAdminRequestDto`: Request body for updating a **Super Admin** profile.

### Response DTOs (Data Transfer Objects)

* `AdminResponseDto`: Response body for retrieving an **Admin** profile.
* `AuthenticationResponseDto`: Response body after successful authentication, containing user details and tokens.
* `CancelClearanceResponseDto`: Response body after a successful cancellation of a clearance request.
* `ClearanceResponseDto`: Response body for a clearance request.
* `CorperResponseDto`: Response body for creating or retrieving a **Corper**.
* `PpaResponseDto`: Response body for creating or retrieving a **PPA**.
* `RejectClearanceDto`: Response body for a rejected clearance request.
* `SuperAdminResponseDto`: Response body for creating or retrieving a **Super Admin**.
* `TokenResponse`: Response body containing `access_token` and `refresh_token`.
* `UnitResponseDto`: Response body for creating or retrieving a **Unit**.

### Generic API Response Structures

These are wrapper objects that provide consistent formatting for all API responses, including status and error messages.

* `ApiResponseStructureAdminResponseDto`
* `ApiResponseStructureAuthenticationResponseDto`
* `ApiResponseStructureCancelClearanceResponseDto`
* `ApiResponseStructureClearanceResponseDto`
* `ApiResponseStructureCorperResponseDto`
* `ApiResponseStructureListAdminResponseDto`
* `ApiResponseStructureListClearanceResponseDto`
* `ApiResponseStructureListCorperResponseDto`
* `ApiResponseStructureListPpaResponseDto`
* `ApiResponseStructureListString`
* `ApiResponseStructureListUnitResponseDto`
* `ApiResponseStructureLong`
* `ApiResponseStructureObject`
* `ApiResponseStructurePpaResponseDto`
* `ApiResponseStructureRejectClearanceDto`
* `ApiResponseStructureString`
* `ApiResponseStructureSuperAdminResponseDto`
* `ApiResponseStructureUnitResponseDto`
* `ApiResponseStructureVoid`