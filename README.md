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



