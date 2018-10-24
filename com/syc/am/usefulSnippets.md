# call procedure in springframework

    import org.springframework.jdbc.core.JdbcTemplate;

    public class ProcedureCaller{

        private JdbcTemplate jdbcTemplate;

        @Autowired
        public void setDataSource(DataSource dataSource) {
            this.jdbcTemplate = new JdbcTemplate(dataSource);
        }

        public Map<String, Object> call(String proc) {
                CallableStatementCreatorFactory cscFactory
                        = new CallableStatementCreatorFactory(proc);

                CallableStatementCreator csc = cscFactory.newCallableStatementCreator(new HashMap<>());
                return jdbcTemplate.call(csc, new ArrayList<>());
        }

        public static void main(String args[]){
            ProcedureCaller pcaller = new ProcedureCaller();
            Map<String, Object> rests = pcaller.call("call databaseName.abc()");
            for (String key : rests.keySet()) {
                System.out.println(key+"====="+rests.get(key));
            }
        }
    }

获得的结果如下(List里面套Map):

    #result-set-4=====[{20/3.=6.6667}]
    #result-set-5=====[{10/3.=3.3333}]
    #result-set-1=====[{now()=2018-01-17 17:47:21.0}]
    #update-count-1=====0
    #result-set-2=====[{CURRENT_TIMESTAMP()=2018-01-17 17:47:27.0}]
    #result-set-3=====[{10/3=3.3333}]

# distinct duplicate key

    List<Transformable> distinctAvoidingDuplicateKey( List<Transformable> objects){
        Map<String, Transformable> distinctMap = new HashMap<>();
        for (Iterator<Transformable> iterator = objects.iterator(); iterator.hasNext(); ) {
            Transformable transformable = iterator.next();
            if(transformable instanceof FoundationDesc){
                FoundationDesc desc = (FoundationDesc) transformable;
                distinctMap.put(desc.loadUniqueKey(),desc);
            }else{
                BaseM baseM = (BaseM) transformable;
                distinctMap.put(baseM.loadUniqueKey(),baseM);
            }
        }
        return new ArrayList<>(distinctMap.values());
    }

去除重复,直接使用HashMap实现
