import React, { useState } from 'react';
import { Grid, Box, Fade, Slide } from '@mui/material';
import { PageLayout } from '@/shared/components/PageLayout';
import { ReportForm } from '@/features/reports/components/ReportForm';
import { ReportResult } from '@/features/reports/components/ReportResult';
import { ReportTypeInfo } from '@/features/reports/components/ReportTypeInfo';
import { useReportGeneration } from '@/features/reports/hooks/useReportGeneration';
import { ReportResponse } from '@/features/reports/types/report.types';

export const ReportsPage: React.FC = () => {
  const [generatedReport, setGeneratedReport] = useState<ReportResponse | null>(null);
  const { generateReport, loading, error } = useReportGeneration();

  const handleGenerateReport = async (params: Parameters<typeof generateReport>[0]) => {
    try {
      const report = await generateReport(params);
      setGeneratedReport(report);
    } catch (err) {
      console.error('Failed to generate report:', err);
    }
  };

  return (
    <PageLayout
      title="Reports"
      subtitle="Generate system reports and analytics"
    >
      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Slide direction="right" in={true} mountOnEnter unmountOnExit timeout={600}>
            <Box>
              <ReportForm
                onGenerate={handleGenerateReport}
                loading={loading}
                error={error}
              />
            </Box>
          </Slide>
        </Grid>

        <Grid item xs={12} md={6}>
          <Slide direction="left" in={true} mountOnEnter unmountOnExit timeout={800}>
            <Box>
              <Fade in={!!generatedReport} timeout={500}>
                <Box>
                  {generatedReport && (
                    <>
                      <ReportResult report={generatedReport} />
                      <Box sx={{ mt: 2 }} />
                    </>
                  )}
                </Box>
              </Fade>
              <ReportTypeInfo />
            </Box>
          </Slide>
        </Grid>
      </Grid>
    </PageLayout>
  );
};