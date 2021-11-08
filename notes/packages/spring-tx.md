# @Transactional
```java
TransactionInterceptor
    TransactionAspectSupport.invokeWithinTransaction()
        TransactionAspectSupport.createTransactionIfNecessary()
        InvocationCallback.proceedWithInvocation()
        // catch
        TransactionAspectSupport.completeTransactionAfterThrowing()
            // conditionally
            TransactionInfo.getTransactionManager().rollback()
        // finally
        TransactionAspectSupport.cleanupTransactionInfo()
        TransactionAspectSupport.commitTransactionAfterReturning()
```
