描述流程：
每过一段时间，刷新scene
每次刷新前处理键盘事件,更新各种gameobject
刷新就是根据各种gameobject的内部状态进行刷新
刷新完之后进行判定，修改状态，渲染

gameengine执行刷新操作while（true）{xxx}
xxx:scene.update
update:handleinput->for each obj update -> check ->渲染
我的scene只是用来打包整个画面的对象，renderer负责render scene先不考虑render文本
gameengine初始化各种配置，比如inputmanager,renderer,scene等
gamelogic 处理input，对scene进行update和check（创建特效对象等）
update是调用每个obj的update,check是针对各种交互事件和边界检查的
character的基础字段包括 存活状态，mode持续时间，是否处于免疫状态，免疫时间，拥有特效列表（owner），速度加速度，位置等
一个character有状态组件（血量血条两个object），有技能组件(一个object列表)。有基本信息，和mode  渲染的时候主体根据mode（分类渲染），组件要渲染

character主体包含了头发，脸，身体，手，腿，脚这几部分，分开管理，但又紧密组合成了character主体部分,用list<身体部件类型>来保存，身体部件类型新创建一个文件夹，分为基类和这些部件，基类声明map<部件状态，相对于character的baseposition的像素分布表格表格包含颜色信息>，每个具体类硬编码map（相当于手工设计不同状态下的形象然后把形象映射到各个部位，每个部位在不同状态下形象不同，但是是由事件触发的，各个部件不同状态下位置的协同是由上层保证的，先不用管）。请你根据我的要求实现框架但不要代码实现

不管创建多少个bodypart，同一种part硬编码出来的stateconfigs是固定的，现在的逻辑不正确，应该用 把statecomfigs设置为static?各个part 的构造方法私有，但是可以

k跳跃，第一次跳起来未落地再k触发二段跳，w上，s下，a左，d右，l冲刺，短摁q技能，j攻击，长摁q回血
w+j  s+j 分别是上批和下批
对于processinput来说，我认为需要

给characterMode增加攻击这一模式修改与之相关的文件，其它都不变
先取出events里决定人物朝向的事件更改，再取出决定charactermode的事件更改，再取出与特效相关的事件进行对应逻辑。只改processinput方法

待定：onground判断，长按回血逻辑