package eu.peppol.persistence.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import eu.peppol.persistence.RepositoryConfiguration;
import eu.peppol.persistence.jdbc.OxalisDataSourceFactoryDbcpImplTest;
import eu.peppol.persistence.jdbc.util.InMemoryDatabaseHelper;
import eu.peppol.util.GlobalConfiguration;
import eu.peppol.util.GlobalConfigurationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IModuleFactory;
import org.testng.ITestContext;

import javax.sql.DataSource;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * TestNG implementation of factory, which provides the Google Guice modules required
 * for running the tests.
 *
 * @author steinar
 *         Date: 16.10.2016
 *         Time: 19.44
 */
public class TestModuleFactory implements IModuleFactory {

    public static final Logger log = LoggerFactory.getLogger(TestModuleFactory.class);

    public static final String CREATE_OXALIS_DBMS_H2_SQL = "sql/create-oxalis-dbms-h2.sql";


    @Override
    public Module createModule(ITestContext iTestContext, Class<?> aClass) {

        String[] includedGroups = iTestContext.getIncludedGroups();

        if (aClass.equals(OxalisDataSourceFactoryDbcpImplTest.class)) {
            return new TestWithoutInmemoryDatasource();
        } else
            return new TestInMemoryDatabaseModule();
    }


    private class TestInMemoryDatabaseModule extends AbstractModule {
        @Override
        protected void configure() {

            binder().install(new RepositoryModule());
        }

        @Provides
        public DataSource provideH2DataSource() {
            return InMemoryDatabaseHelper.createInMemoryDatabase();
        }

        @Provides
        RepositoryConfiguration repositoryConfiguration() {
            return getDummyRepositoryConfiguration();
        }
    }

    /**
     * Guice memory module, which uses the globally configured data source.
     */
    class TestWithoutInmemoryDatasource extends AbstractModule {

        @Override
        protected void configure() {
            binder().install(new RepositoryModule());
            binder().install(new OxalisDataSourceModule());

        }

        @Provides @javax.inject.Singleton
        GlobalConfiguration providesGlobalConfiguration() {
            return GlobalConfigurationImpl.getInstance();
        }


        @Provides
        RepositoryConfiguration repositoryConfiguration(GlobalConfiguration c) {
            return new RepositoryConfiguration() {
                @Override
                public Path getBasePath() {
                    return Paths.get(c.getInboundMessageStore());
                }

                @Override
                public URI getJdbcConnectionUri() {
                    return URI.create(c.getJdbcConnectionURI());
                }

                @Override
                public String getJdbcDriverClassPath() {
                    return c.getJdbcDriverClassPath();
                }

                @Override
                public String getJdbcDriverClassName() {
                    return c.getJdbcDriverClassName();
                }

                @Override
                public String getJdbcUsername() {
                    return c.getJdbcUsername();
                }

                @Override
                public String getJdbcPassword() {
                    return c.getJdbcPassword();
                }

                @Override
                public String getValidationQuery() {
                    return c.getValidationQuery();
                }
            };
        }


    }
    static RepositoryConfiguration getDummyRepositoryConfiguration() {
        return new RepositoryConfiguration() {
            @Override
            public Path getBasePath() {
                return Paths.get("/var/peppol");
            }

            @Override
            public URI getJdbcConnectionUri() {
                return null;
            }

            @Override
            public String getJdbcDriverClassPath() {
                return null;
            }

            @Override
            public String getJdbcDriverClassName() {
                return null;
            }

            @Override
            public String getJdbcUsername() {
                return null;
            }

            @Override
            public String getJdbcPassword() {
                return null;
            }

            @Override
            public String getValidationQuery() {
                return null;
            }
        };
    }

}
