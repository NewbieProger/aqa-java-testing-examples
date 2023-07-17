camunda-zeebe-client-java and camunda-operate-client-java for automating testing, and it's easy for updating for new endpoints

The operate client is based on feign where responses are decorated to io.restassured.response.Response for easy verifying

Small guide to start:

- Set env
  in [ZeebeClientProvider.java](../zeebe-operate-feign-client/src/main/java/zeebe/client/ZeebeClientProvider.java)
  and [ZeebeClientProvider.java](../zeebe-operate-feign-client/src/main/java/zeebe/feign/client/OperateFeignClientProvider.java)
- Put `collectOperateProcessDefinitionKeysMap()` in your runner
- Profit!

Tips:

- Don't try to delete processes from operate. It has problems via async
- The operateDto is based on some version of OperateClient and if some new endpoints were added - feel free to update it :)
- Yes, it's OK if you find something like `check incidents`, but I use not "incident endpoint" because operate built
  so :)
