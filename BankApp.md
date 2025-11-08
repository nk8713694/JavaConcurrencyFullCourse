# 🏦 Bank Application - Spring Boot Interview Preparation Guide

## 📋 Table of Contents
1. [Overview](#overview)
2. [Architecture & Design Patterns](#architecture--design-patterns)
3. [Project Structure](#project-structure)
4. [Code Implementation](#code-implementation)
5. [Key Concepts](#key-concepts)
6. [Testing](#testing)
7. [Interview Questions & Answers](#interview-questions--answers)
8. [API Documentation](#api-documentation)

---

## 🎯 Overview

This is a **Spring Boot RESTful Banking Application** that demonstrates:
- Money transfer between accounts
- Transaction management with `@Transactional`
- Global exception handling
- Unit testing with Mockito
- JPA/Hibernate for database operations
- RESTful API design

**Tech Stack:**
- Spring Boot 3.5.6
- Spring Data JPA
- Hibernate
- H2/MySQL Database
- Lombok
- JUnit 5 + Mockito
- Maven

---

## 🏗️ Architecture & Design Patterns

### 1. **Layered Architecture**
```
Controller Layer (REST API)
      ↓
Service Layer (Business Logic)
      ↓
Repository Layer (Data Access)
      ↓
Database (JPA/Hibernate)
```

### 2. **Design Patterns Used**

#### a) **Repository Pattern**
- Abstracts data access logic
- Provides CRUD operations via Spring Data JPA

#### b) **Dependency Injection (DI)**
- Uses `@Autowired` for loose coupling
- Constructor/Field injection

#### c) **DTO Pattern**
- `TransferRequest` - Data Transfer Object for API requests

#### d) **Exception Handling Strategy**
- Custom exceptions: `ResourceNotFoundException`, `InsufficientBalanceException`
- Global exception handler with `@RestControllerAdvice`

---

## 📁 Project Structure

```
handsOnBankApplication/
├── Account.java                    // Entity class
├── AccountRepository.java          // Data access layer
├── TransferService.java            // Business logic layer
├── AccountController.java          // REST API layer
├── TransferRequest.java            // DTO
├── ResourceNotFoundException.java  // Custom exception
├── InsufficientBalanceException.java // Custom exception
├── GlobalExceptionHandler.java     // Centralized exception handling
└── test/
    └── TransferServiceTest.java    // Unit tests
```

---

## 💻 Code Implementation

### 1️⃣ **Entity Class - Account.java**

```java
package handsOnBankApplication;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
    private String name;
    
    private BigDecimal balance;
}
```

**Key Points:**
- `@Entity` - Marks as JPA entity (maps to database table)
- `@Id` - Primary key
- `@GeneratedValue(strategy = GenerationType.AUTO)` - Auto-increment ID
- `@Data` (Lombok) - Auto-generates getters, setters, toString, equals, hashCode
- `BigDecimal` - Used for precise monetary calculations (not float/double)

**Interview Q:** *Why BigDecimal for money?*
- Floating-point types (float/double) have precision issues
- `0.1 + 0.2 != 0.3` in floating-point arithmetic
- BigDecimal provides exact decimal representation

---

### 2️⃣ **Repository - AccountRepository.java**

```java
package handsOnBankApplication;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
}
```

**Key Points:**
- Extends `JpaRepository<Entity, PrimaryKeyType>`
- No implementation needed - Spring Data JPA provides it
- Inherits methods: `findById()`, `save()`, `findAll()`, `delete()`, etc.

**Interview Q:** *What is Spring Data JPA?*
- Abstraction over JPA (Hibernate implementation)
- Reduces boilerplate code
- Provides repository pattern out-of-the-box

---

### 3️⃣ **Service Layer - TransferService.java**

```java
package handsOnBankApplication;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
@Transactional
public class TransferService {

    @Autowired
    private AccountRepository accountRepo;

    public void transferMoney(int fromId, int toId, BigDecimal amount) {
        // 1. Fetch sender account
        Account fromAccount = accountRepo.findById(fromId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender account not found"));

        // 2. Fetch receiver account
        Account toAccount = accountRepo.findById(toId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver account not found"));

        // 3. Validate sufficient balance
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in sender's account");
        }

        // 4. Deduct from sender
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        
        // 5. Add to receiver
        toAccount.setBalance(toAccount.getBalance().add(amount));

        // 6. Save both accounts
        accountRepo.save(fromAccount);
        accountRepo.save(toAccount);
    }
}
```

**Key Points:**

#### `@Service`
- Marks as Spring-managed bean
- Business logic layer
- Auto-scanned by Spring component scanning

#### `@Transactional` ⭐ **MOST IMPORTANT**
- Ensures ACID properties
- If any exception occurs, entire transaction rolls back
- Both debit and credit happen atomically

**Interview Q:** *What happens if transfer fails?*
- Without `@Transactional`: Money deducted from sender but not added to receiver ❌
- With `@Transactional`: Both operations rollback, no money lost ✅

#### `Optional<Account>` and `orElseThrow()`
```java
accountRepo.findById(fromId)
    .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
```
- `findById()` returns `Optional<Account>` (may or may not have value)
- `.orElseThrow()` - If empty, throw exception; else return value
- Avoids `NullPointerException`

#### `BigDecimal.compareTo()`
```java
if (fromAccount.getBalance().compareTo(amount) < 0)
```
- Returns: -1 (less), 0 (equal), 1 (greater)
- Use `compareTo()` instead of `==` for BigDecimal

---

### 4️⃣ **Controller - AccountController.java**

```java
package handsOnBankApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private TransferService accountService;

    @PostMapping("/transferamount")
    public ResponseEntity<String> transferAmount(@RequestBody TransferRequest request) {
        try {
            accountService.transferMoney(
                request.getFromAccountId(), 
                request.getToAccountId(), 
                request.getAmount()
            );
            return ResponseEntity.ok("Transfer successful");
        } catch (ResourceNotFoundException | InsufficientBalanceException e) {
            return ResponseEntity.badRequest().body("Transfer failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
```

**Key Points:**

#### `@RestController`
- Combination of `@Controller` + `@ResponseBody`
- Returns JSON/XML directly (not view names)

#### `@RequestMapping("/api")`
- Base path for all endpoints in this controller
- Full path: `http://localhost:8080/api/transferamount`

#### `@PostMapping("/transferamount")`
- Maps HTTP POST requests
- Alternatives: `@GetMapping`, `@PutMapping`, `@DeleteMapping`

#### `@RequestBody`
- Converts JSON request body to Java object
- Uses Jackson library for JSON ↔ Object conversion

#### `ResponseEntity<String>`
- Full control over HTTP response
- Can set status code, headers, body

---

### 5️⃣ **DTO - TransferRequest.java**

```java
package handsOnBankApplication;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferRequest {
    int fromAccountId;
    int toAccountId;
    BigDecimal amount;
}
```

**Sample JSON Request:**
```json
{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 500.00
}
```

---

### 6️⃣ **Custom Exceptions**

#### ResourceNotFoundException.java
```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

#### InsufficientBalanceException.java
```java
public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
```

**Interview Q:** *Why extend RuntimeException?*
- `RuntimeException` = Unchecked exception (no need for try-catch or throws declaration)
- `Exception` = Checked exception (must handle explicitly)
- Spring recommends unchecked exceptions for cleaner code

---

### 7️⃣ **Global Exception Handler**

```java
package handsOnBankApplication;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<String> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
```

**Key Points:**

#### `@RestControllerAdvice`
- Global exception handler for all controllers
- Centralizes exception handling logic
- Combination of `@ControllerAdvice` + `@ResponseBody`

#### `@ExceptionHandler(ExceptionType.class)`
- Catches specific exception types
- Returns custom HTTP responses

**Benefits:**
- ✅ Cleaner controller code (no try-catch)
- ✅ Consistent error responses
- ✅ Separation of concerns

---

## 🧪 Testing

### Unit Test - TransferServiceTest.java

```java
package handsOnBankApplication;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransferServiceTest {

    @InjectMocks
    private TransferService transferService;

    @Mock
    private AccountRepository accountRepo;

    @Test
    public void testSuccessfulTransfer() {
        // Arrange
        Account from = new Account();
        from.setId(1);
        from.setBalance(new BigDecimal("1000"));

        Account to = new Account();
        to.setId(2);
        to.setBalance(new BigDecimal("500"));

        when(accountRepo.findById(1)).thenReturn(Optional.of(from));
        when(accountRepo.findById(2)).thenReturn(Optional.of(to));
        when(accountRepo.save(any(Account.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        transferService.transferMoney(1, 2, new BigDecimal("200"));

        // Assert
        assertEquals(new BigDecimal("800"), from.getBalance());
        assertEquals(new BigDecimal("700"), to.getBalance());
    }

    @Test
    public void testInsufficientBalance() {
        // Arrange
        Account from = new Account();
        from.setBalance(new BigDecimal("100"));

        Account to = new Account();
        to.setBalance(new BigDecimal("500"));

        when(accountRepo.findById(1)).thenReturn(Optional.of(from));
        when(accountRepo.findById(2)).thenReturn(Optional.of(to));

        // Act & Assert
        assertThrows(InsufficientBalanceException.class, () ->
                transferService.transferMoney(1, 2, new BigDecimal("200"))
        );
    }
}
```

### Testing Concepts Explained

#### 1. **Mockito Annotations**

##### `@ExtendWith(MockitoExtension.class)`
- Enables Mockito in JUnit 5
- Initializes mocks before each test

##### `@Mock`
- Creates a mock object (fake implementation)
- `AccountRepository` is mocked - doesn't hit real database

##### `@InjectMocks`
- Creates instance and injects mocked dependencies
- `TransferService` gets the mocked `AccountRepository`

#### 2. **Mocking Behavior**

```java
when(accountRepo.findById(1)).thenReturn(Optional.of(from));
```
- **When** `findById(1)` is called **Then** return `Optional.of(from)`
- Simulates repository behavior without database

```java
when(accountRepo.save(any(Account.class))).thenAnswer(i -> i.getArguments()[0]);
```
- **When** `save()` is called with any Account object
- **Then** return the same object passed as argument
- `i.getArguments()[0]` - Get first argument from method call

**Interview Q:** *Why return the argument in save()?*
- Real repository returns saved entity (possibly with updated ID)
- Mock simulates this behavior
- Allows assertions on the modified objects

#### 3. **AAA Pattern**
- **Arrange** - Setup test data and mocks
- **Act** - Execute method under test
- **Assert** - Verify expected outcome

#### 4. **Assertions**

```java
assertEquals(expected, actual);
assertThrows(ExceptionType.class, () -> code);
```

---

## 🔑 Key Concepts

### 1. **Transaction Management**

#### ACID Properties:
- **Atomicity** - All or nothing
- **Consistency** - Valid state always
- **Isolation** - Concurrent transactions don't interfere
- **Durability** - Changes persist after commit

#### `@Transactional` Behavior:
```java
@Transactional
public void transferMoney(...) {
    // Step 1: Debit sender
    fromAccount.setBalance(...);
    accountRepo.save(fromAccount);
    
    // Exception here? Both operations rollback!
    
    // Step 2: Credit receiver
    toAccount.setBalance(...);
    accountRepo.save(toAccount);
}
```

### 2. **Dependency Injection**

#### Field Injection (Used in code)
```java
@Autowired
private AccountRepository accountRepo;
```

#### Constructor Injection (Recommended)
```java
private final AccountRepository accountRepo;

public TransferService(AccountRepository accountRepo) {
    this.accountRepo = accountRepo;
}
```

**Interview Q:** *Which is better?*
- Constructor injection is preferred (immutability, testability)
- Field injection is simpler but harder to test

### 3. **Spring Data JPA**

#### Repository Hierarchy:
```
Repository (marker interface)
    ↓
CrudRepository (basic CRUD)
    ↓
PagingAndSortingRepository (pagination)
    ↓
JpaRepository (JPA-specific methods)
```

#### Auto-generated Queries:
```java
// No implementation needed!
Optional<Account> findById(Integer id);
Account save(Account account);
void deleteById(Integer id);
List<Account> findAll();
```

---

## 📚 Interview Questions & Answers

### Q1: Explain the flow of a transfer request from client to database.

**Answer:**
```
1. Client sends POST request → /api/transferamount
2. AccountController receives @RequestBody TransferRequest
3. Controller calls TransferService.transferMoney()
4. Service fetches both accounts using AccountRepository
5. Service validates balance
6. Service updates balances and saves (within @Transactional)
7. Controller returns ResponseEntity with success/error message
8. If exception occurs, GlobalExceptionHandler catches it
```

---

### Q2: What happens if the application crashes during a transfer?

**Answer:**
- **With `@Transactional`**: 
  - Transaction not committed → Rollback
  - Database remains consistent
  - Money not lost ✅

- **Without `@Transactional`**:
  - Partial update possible
  - Sender debited, receiver not credited ❌
  - Data inconsistency!

---

### Q3: How does Mockito's `thenAnswer` work?

**Answer:**
```java
when(accountRepo.save(any(Account.class)))
    .thenAnswer(i -> i.getArguments()[0]);
```
- `i` is `InvocationOnMock` - contains method call details
- `i.getArguments()` returns Object[] of all arguments
- `[0]` gets first argument (the Account object)
- Returns same object, simulating repository's save behavior

**Alternative:**
```java
.thenReturn(account); // Returns specific object
.thenAnswer(invocation -> invocation.getArgument(0)); // Newer API
```

---

### Q4: Why use Optional instead of null?

**Answer:**
```java
// Bad (null check required)
Account account = accountRepo.findById(1);
if (account == null) {
    throw new Exception("Not found");
}

// Good (explicit handling)
Account account = accountRepo.findById(1)
    .orElseThrow(() -> new ResourceNotFoundException("Not found"));
```
- **Avoids NullPointerException**
- **Forces explicit handling** of missing values
- **Functional programming style** (.map, .filter, etc.)

---

### Q5: Difference between @RestController and @Controller?

**Answer:**
- **`@Controller`**: Returns view names (HTML templates)
- **`@RestController`** = `@Controller` + `@ResponseBody`
  - Returns data (JSON/XML) directly
  - No view resolution

---

### Q6: How to handle concurrent transfers (race condition)?

**Answer:**

**Problem:**
```
User A balance: $100
Thread 1: Transfer $50 to B
Thread 2: Transfer $60 to C
Both read balance=$100, both succeed → Overdrawn!
```

**Solutions:**

1. **Pessimistic Locking:**
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT a FROM Account a WHERE a.id = :id")
Account findByIdForUpdate(@Param("id") int id);
```

2. **Optimistic Locking:**
```java
@Entity
public class Account {
    @Version
    private Long version;
}
```

3. **Database Constraints:**
```sql
ALTER TABLE account ADD CONSTRAINT balance_positive CHECK (balance >= 0);
```

---

### Q7: What are the drawbacks of field injection?

**Answer:**
```java
// Field injection (current code)
@Autowired
private AccountRepository accountRepo;
```

**Issues:**
- ❌ Cannot make fields `final` (immutability)
- ❌ Harder to write unit tests (need reflection)
- ❌ Hidden dependencies (not visible in constructor)

**Better:**
```java
private final AccountRepository accountRepo;

public TransferService(AccountRepository accountRepo) {
    this.accountRepo = accountRepo;
}
```

---

### Q8: Explain transaction propagation.

**Answer:**
```java
@Transactional(propagation = Propagation.REQUIRED) // Default
public void transferMoney() { ... }
```

**Types:**
- **REQUIRED**: Join existing transaction or create new
- **REQUIRES_NEW**: Always create new transaction
- **MANDATORY**: Must have existing transaction (else error)
- **NEVER**: Execute outside transaction
- **SUPPORTS**: Join if exists, else non-transactional

---

## 🌐 API Documentation

### Transfer Money

**Endpoint:** `POST /api/transferamount`

**Request Body:**
```json
{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 500.00
}
```

**Success Response (200 OK):**
```json
"Transfer successful"
```

**Error Responses:**

**Account Not Found (404):**
```json
"Sender account not found"
```

**Insufficient Balance (400):**
```json
"Insufficient balance in sender's account"
```

---

## 🚀 How to Run

### 1. Clone and Build
```bash
cd ParkingLot
mvn clean install
```

### 2. Run Application
```bash
mvn spring-boot:run
```

### 3. Test with Postman/cURL
```bash
curl -X POST http://localhost:8080/api/transferamount \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountId": 1,
    "toAccountId": 2,
    "amount": 100.50
  }'
```

### 4. Access H2 Console
```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:parkinglotdb
Username: sa
Password: (leave blank)
```

---

## 📝 Additional Interview Tips

### 1. **Explain Your Design Decisions**
- Why BigDecimal? (Precision)
- Why @Transactional? (Atomicity)
- Why Optional? (Null safety)

### 2. **Know Your Annotations**
- `@Entity`, `@Service`, `@Repository`, `@RestController`
- `@Transactional`, `@ExceptionHandler`
- `@Mock`, `@InjectMocks`

### 3. **Understand Testing**
- Unit tests vs Integration tests
- Mocking vs Real database
- AAA pattern

### 4. **Performance Considerations**
- Database indexes on frequently queried columns
- Connection pooling (HikariCP)
- Caching strategies (@Cacheable)

### 5. **Security Aspects** (If asked)
- Add authentication (Spring Security)
- Encrypt sensitive data
- Input validation (@Valid, @NotNull)
- SQL injection prevention (JPA handles it)

---

## 🎓 Summary

This banking application demonstrates:
✅ **Spring Boot RESTful API**  
✅ **Transaction Management**  
✅ **Exception Handling**  
✅ **Unit Testing with Mockito**  
✅ **Clean Architecture (Layered)**  
✅ **JPA/Hibernate ORM**  

**Master these concepts and you'll ace Spring Boot interviews!** 🚀

---

## 📖 Further Reading

- [Spring Boot Official Docs](https://spring.io/projects/spring-boot)
- [Spring Data JPA Guide](https://spring.io/guides/gs/accessing-data-jpa/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Transaction Management](https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#transaction)

---

**Good luck with your interviews! 💪**

