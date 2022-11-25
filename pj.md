# 代码静态缺陷追踪系统

## 原始需求：

- 代码静态扫描工具能检测出代码中的静态缺陷
- 静态缺陷**什么时候、由谁引入**。代码修改的历史对目前判断有影响。
- 需存储各个版本（按commit为单位）的静态缺陷信息
  - commit是一组代码修改的提交操作，对应提交前、后两个版本
  - 习惯上用commit指代提交后版本。
- 对任意版本任意缺陷，能找到前后版本中对应的静态缺陷。

## 静态缺陷案例

随着代码版本的演进，后一个版本上的静态缺陷实例可能与前一个版本的某个静态缺陷实例是“同一个”。即具有追溯关系、表示同一个静态缺陷实例的集合，我们称为静态缺陷案例，即**静态缺陷追溯链**。

缺陷实例匹配：通过比较当前commit的缺陷实例与父commit缺陷实例的相关信息确定这两个缺陷实例是否是匹配状态。

- 进行匹配操作后，后一个commit中缺陷就有了相关案例状态。

  ![image-20221123163919068](C:\Users\98157\AppData\Roaming\Typora\typora-user-images\image-20221123163919068.png)

#### 初步关系模式

![image-20221123163957737](C:\Users\98157\AppData\Roaming\Typora\typora-user-images\image-20221123163957737.png)

![image-20221123164039445](C:\Users\98157\AppData\Roaming\Typora\typora-user-images\image-20221123164039445.png)

## 数据分析需求：

- 最新版本中，静态缺陷数量的分类统计和详细列表：
  - 按类型统计
  - 详情按存续时长排序
  - 按类型统计存续时长的平均值和中位数。
- 指定版本代码快照中：
  - 静态缺陷引入、消除情况的分类统计和详细列表。
- 指定时间段内
  - 静态缺陷引入、消除情况的分类统计和详细列表。
- 数据分析统计：
  - 指定时间段内引入静态缺陷的数量，解决情况
    - 包括解决率、解决所用的时间，按总量以及分各个缺陷大类和具体类型统计。
  - 现存静态缺陷中，已经存续超过指定时长的分类情况统计
  - 某个开发人员引入缺陷、解决他人引入缺陷、自己引入且尚未解决缺陷、自己引入且被他人解决缺陷的分类统计，存活周期统计。
  - 统计数据需要有相应的详细信息。



![image-20221123164846416](C:\Users\98157\AppData\Roaming\Typora\typora-user-images\image-20221123164846416.png)

![image-20221123164859023](C:\Users\98157\AppData\Roaming\Typora\typora-user-images\image-20221123164859023.png)

## 具体数据分析案例

最新版本代码快照中，静态缺陷引入、消除情况的分类统计和详细列表。

- 引入数（总共引入多少静态缺陷）

  `select count(*) from iss_match where status = 'NEW'`

- 分类引入数（每种缺陷类型分别引入多少静态缺陷）

  `select type_id, count(*) from iss_match as m, iss_instance as inst where m.inst_id = inst.id, and status = 'NEW' and m.commit_hash = 'abcd' group by type_id`

- 分类引入详细列表，包括类型、描述、文件、类、方法、起始行列，按类型、未见、起始行列排序

  `select type, description, file_path, class, method, start_line, start_col from iss_instance as inst, iss_match as m, iss_location as loc where inst.id=loc.inst_id and inst.id=m.inst_id and status=‘NEW’ and m.commit_hash=‘abcd’ order by type, file_path, start_line, start_col`

- 按类型统计：（要求iss_case中的commit_hash_last必须要刷新）

  `select type_id, count(*) from iss_case where commit_hash_last = ‘123456’ and case_status = ‘UNSOLVED`

如何动态找最新版本？

- 不建议直接找max time
- 使用专门的版本表 iss_commit



- 详情按存续时长排序（现存未解决的静态缺陷按新增时间从小到大排，并给出到当前的存续时间）

  `select case_id, type_id, time_new, sysdate()-time_new from iss_case where commit_hash_last = ‘123456’ and case_status = ‘UNSOLVED’  order by time_new`

系统时间通常可以根据所使用的DBMS获取，注意系统时间可能的副作用（NOW总是在变）。



## 静态缺陷追踪系统基础数据准备

- 尝试创建iss_instance, iss_location, iss_match, iss_case四张表
  - 字段类型自行设计
  - 假设目前只支持但代码库、单个扫描工具、单一语言
- 写出创建数据库的脚本
- 原始数据准备

## 从应用到设计

![image-20221123170214135](C:\Users\98157\AppData\Roaming\Typora\typora-user-images\image-20221123170214135.png)

要求：

- 高内聚：
  - 每个数据对象都表达属于自己部分的信息
  - 数据对象之间的关系使用专门方式进行表达，不混杂。
- 低耦合：
  - 数据对象之间相互影响最小
  - 降低数据冗余

案例分析：

- 主键选取问题
  - 缺陷的ID？提交的ID？
- 围绕业务实体本身，必要时添加“人造”属性，注意人造属性的合理性
- 业务功能范围、优先级、迭代问题
  - 基于目标场景识别数据需求
    - 系统要达到什么目的
    - 基本功能是什么
    - 涉及哪些数据
  - 根据业务价值选择优先实现的数据需求
    - 最有价值的内容是什么?
  - 随着对需求和系统认知的不断深入，迭代规划
    - 1-2周的短期迭代
    - 迭代目标明确，边界清晰
    - 灵活（但受控）地调整计划

![image-20221123170656747](C:\Users\98157\AppData\Roaming\Typora\typora-user-images\image-20221123170656747.png)

## 需求

- 数据的查询
  - 给定版本（commit）上静态缺陷的存在情况，以及已经存续的时间
  - 一段时间内静态缺陷的引入和消除情况。
- 数据的表示
  - 每个版本的静态缺陷
  - 前后版本静态缺陷的追踪关系
  - 一个仓内考虑单个目标分支
    - 但会有多分支的合并
- 具体考虑
  - 一个静态缺陷可以表现在代码文件中的多出
    - flow
  - 同一个静态缺陷在不同版本上出现
    - 假设已经有一个简单的版本间匹配算法：通过上下文能分辨出某个静态缺陷对应前一个版本的哪个缺陷。
  - 不同仓、不同分支上可以有相同的commit
  - 静态缺陷状态变化的历史

## 术语体系

- 制定团队内部的术语体系
  - 静态缺陷相关
    - issue/issueCase, IssueInstance...
  - 历史追溯相关
    - match, commit, time, commiter
- 制定表的命名规则
  - 名称，例如issue_instance
  - 类型_名称，例如iss_match, t_instance

## 从ER建模到表结构

- 实体集
  - 直接转化为表
- 联系集
  - 多对多：直接转化为表
  - 一对多：原则上不需要额外加表，但需要考虑是否为全参与
  - 一对一：复查设计
- 特殊情况：
  - 多值属性：关注实体集本身
  - 弱实体集：强烈的附属感
  - 特化/泛化：考虑必要性

## 数据分析需求的其他思考

- 与merge相关
  - 考虑合并节点上，静态缺陷相对于不同的parent commit有不同的变化状态。
- 与跨代码库、跨分支相关
  - 不同代码库中commit hash相同时，如何高效处理和分析？
  - 扫描时按给定的branch进行扫描，若不同branch中commit hash相同时，如何高效处理和分析？

## 性能与可靠性需求

- 性能
  - 支持代码仓的代码规模百万行级，提交数量500+
    - 一般企业级项目的更新频度和强度
    - 支持静态缺陷数量（跨版本总量）在百万甚至千万级别以上
  - 冷启动时间在合理的较长时间内(比如1-2天)
  - 单库日常扫描（代码增量扫描）单版本数据更新在分钟级
- 可靠性
  - 考虑到建立版本间映射算法准确性，应能按需更新算法并刷新数据
  - 因为调用静态扫描工具时，扫描速度甚至正确性都不可控，因此为了便于修复，需要原始数据入库与扫描单独运行。

## 静态缺陷版本间映射问题

- 问题描述
  - 确定两个版本快照中，哪些静态缺陷是“同一个”
- 判断思想
  - 静态缺陷在单个文件中
  - 文件的前后版本对应关系由Git提供
  - 对文件中所有静态缺陷进行版本间匹配，得到对应关系
- 实现方式
  - 采用提供的jar包进行二次封装
  - 也可自行实现简单的映射算法

## 多版本的处理问题

- 静态缺陷是否全数入库？
  - 数量庞大
  - 版本间增量大小不一
- 静态缺陷的生命周期如何表示？
  - 引入
  - 最后一次出现
  - 消除
  - 再次引入（？）
- 静态缺陷的增量扫描问题
  - 没有变化的文件中静态缺陷也不会发生任何变化